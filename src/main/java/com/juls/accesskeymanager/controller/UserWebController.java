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
import com.juls.accesskeymanager.data.events.SendEmailEvent;
import com.juls.accesskeymanager.data.models.AuthenticationRequest;
import com.juls.accesskeymanager.data.models.EmailRequest;
import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.data.token.VerificationToken;
import com.juls.accesskeymanager.exceptions.NotFoundException;
import com.juls.accesskeymanager.services.EmailService;
import com.juls.accesskeymanager.services.UserServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class UserWebController {
    
    private final UserServiceImpl userService;
    private final ApplicationEventPublisher publisher;
    private final EmailService emailService;
    

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody AuthenticationRequest authenticationRequest, final HttpServletRequest request){
        try {
            Users user = this.userService.registerUser(authenticationRequest);
            this.publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request))); 
            new ResponseEntity<>(HttpStatus.ACCEPTED);
            return ResponseEntity.ok("User registered successfully"); 
        } catch (Exception e) {
            log.debug(e.getMessage());      
            new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        // return null;
    }

    private String applicationUrl(HttpServletRequest request){
        String url = String.format("http://%s:%s%s",request.getServerName(),request.getServerPort(),request.getContextPath());
        return url;
    }

    @SuppressWarnings({ "static-access", "finally" })
    @PostMapping("/verifyuser")
    public ResponseEntity<String> verifyUser(@RequestParam(value = "token") String token){
        try {
            VerificationToken verificationToken = this.userService.findToken(token);
            if(verificationToken.getUser().isEnabled()){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN).badRequest().body("User is already verified! Please login");
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
            String resetLink = this.userService.resetPasswordInit(email, applicationUrl(request));
            log.info("Click on the following link to reset your password: {}",resetLink);
            return ResponseEntity.ok().body("Reset Request has been sent to your email successfulyy");
        } catch (Exception e) {
            log.debug(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND).badRequest().body(e.getMessage());
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
    public ResponseEntity<String> updatePassword(@RequestParam("password")String password, @RequestParam("token") String token){
        try {
            String email = this.userService.getUserByToken(token).getEmail();
            boolean isUpdated = this.userService.updatePassword(token, password);
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
    
    @PostMapping("/resendToken")
    public String checkSomething(){
        return "I am checking something";
    }


}
