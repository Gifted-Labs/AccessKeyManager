package com.juls.accesskeymanager.controller;


import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juls.accesskeymanager.data.events.RegistrationCompleteEvent;
import com.juls.accesskeymanager.data.models.AuthenticationRequest;
import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.data.token.VerificationToken;
import com.juls.accesskeymanager.exceptions.NotFoundException;
import com.juls.accesskeymanager.services.UserServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserServiceImpl userService;
    private final ApplicationEventPublisher publisher;
    

    
    /** 
     * @param authenticationRequest
     * @param request
     * @return ResponseEntity<String>
     */

    
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody AuthenticationRequest authenticationRequest, final HttpServletRequest request){
        try {
            Users user = this.userService.registerUser(authenticationRequest);
            String requestType = "api";
            this.publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request),requestType)); 
            new ResponseEntity<>(HttpStatus.ACCEPTED);
            return ResponseEntity.ok("User registered successfully"); 
        } catch (Exception e) {
            log.debug(e.getMessage());      
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/resendVerification")
    public ResponseEntity<String> resendVerificationToken(@RequestParam("email") String email, final HttpServletRequest request){
        try {
            var user = this.userService.checkUser(email);
            String requestType = "api";
            this.publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request),requestType));
            new ResponseEntity<>(HttpStatus.OK);
            return ResponseEntity.ok("Token has been resent to your email");
        }
        catch (Exception e){
            log.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/resendreset")
        public ResponseEntity<String> resendReset(@RequestParam(value = "email")String email, final HttpServletRequest request) throws NotFoundException{
            try {
                if (this.userService.verifyUser(email)){
                String resetLink = this.userService.resetPasswordInit(email, applicationUrl(request),"api");
                log.info("Click on the following link to reset your password: {}",resetLink);
                return ResponseEntity.ok().body("Reset Request has been sent to your email successfulyy");
            }
                else {
                    return ResponseEntity.badRequest().body("User does not exit");
                }

            } catch (Exception e) {
                log.debug(e.getMessage());
                return ResponseEntity.badRequest().body(e.getMessage());
            }
                 
            }
    

    private String applicationUrl(HttpServletRequest request){
        String url = String.format("http://%s:%s%s",request.getServerName(),request.getServerPort(),request.getContextPath());
        return url;
    }

    @SuppressWarnings({ "static-access"})
    @PostMapping("/verifyuser")
    public ResponseEntity<String> verifyUser(@RequestParam(value = "token") String token){
        try {
            VerificationToken verificationToken = this.userService.findToken(token);
            if(verificationToken.getUser().isEnabled()){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);                
            }
            String verificationResult = this.userService.validateToken(token);
            if (verificationResult.equalsIgnoreCase("valid")){
                new ResponseEntity<>(HttpStatus.OK);
                return ResponseEntity.ok().body("Email verification successful. Please Login");
            }            
        } catch (Exception e) {
            log.debug(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST).badRequest().body(e.getMessage());   
        }
        return null;
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetInit(@RequestParam(value = "email")String email, final HttpServletRequest request) throws NotFoundException{
        try {
            String resetLink = this.userService.resetPasswordInit(email, applicationUrl(request),"api");
            log.info("Click on the following link to reset your password: {}",resetLink);
            return ResponseEntity.ok().body("Reset Request has been sent to your email successfulyy");
        } catch (Exception e) {
            log.debug(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
             
        }
    

    @PostMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(@RequestParam(value="token") String token,@RequestParam("password") String password) throws URISyntaxException{
        String validationStatus = this.userService.validateResetToken(token);
        HttpHeaders headers = new HttpHeaders();
        if ("valid".equalsIgnoreCase(validationStatus)){
            URI uri = new URI("/register/update?password="+password+"&token="+token);
            headers.setLocation(uri);
            return new ResponseEntity<>(headers,HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    
    
    @GetMapping("/update")
    public ResponseEntity<String> updatePassword(@RequestParam("password")String password, @RequestParam("confirm") String confirm, @RequestParam("token") String token){
        try {
            String email = this.userService.getUserByToken(token).getEmail();
            boolean isUpdated = this.userService.updatePassword(email, password,confirm);
            if (isUpdated){
                new ResponseEntity<>(HttpStatus.OK);
                return ResponseEntity.ok().body("Password updated successfully. Login with your new password");
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
            new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return null;
    }
}
