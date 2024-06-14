package com.juls.accesskeymanager.web.controllers;       

import java.util.List;

import com.juls.accesskeymanager.exceptions.BadRequestException;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyProperties.AssertingParty.Verification;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.juls.accesskeymanager.data.events.RegistrationCompleteEvent;
import com.juls.accesskeymanager.data.models.AccessKeyDetails;
import com.juls.accesskeymanager.data.models.AuthenticationRequest;
import com.juls.accesskeymanager.data.token.VerificationToken;
import com.juls.accesskeymanager.exceptions.NotFoundException;
import com.juls.accesskeymanager.services.AccessKeyService;
import com.juls.accesskeymanager.services.UserServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/public")
@RequiredArgsConstructor
public class WebController {
    
    public final UserServiceImpl userService;
    public final AccessKeyService accessKeyService;
    public final ApplicationEventPublisher publisher;
     private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String getLoginPage(){
        return "login";
    }


    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }

    @PostMapping("/registeruser")
    public String addUser(@ModelAttribute AuthenticationRequest authenticationRequest,Model model, final HttpServletRequest request){
        try {
            var user = this.userService.registerUser(authenticationRequest);
            String requestType = "web";
            this.publisher.publishEvent(new RegistrationCompleteEvent(user,applicationUrl(request),requestType));
            return "redirect:/public/registration-success?email="+authenticationRequest.email();  
        }
        catch (Exception e){
            log.info("The error message {}",e.getMessage());
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
            model.addAttribute("error", e.getMessage());
            return "error";
            
        }
    }

    private String applicationUrl(HttpServletRequest request){
        String url = String.format("http://%s:%s%s",request.getServerName(),request.getServerPort(),request.getContextPath());
        return url;
    }

    @GetMapping("/verification")
    public String verification(@RequestParam("token") String token, Model model){
        try {
            VerificationToken verificationToken = this.userService.findToken(token);
            if (verificationToken.getUser().isEnabled()){
                model.addAttribute("error","User is already verified. Proceed to login");
            }
            String validationResult = this.userService.validateToken(token);
            if("valid".equalsIgnoreCase(validationResult)){
                return "success-registration";
            }
            else
                return "login";
        } catch (Exception e) {
            log.info("User already: {}",e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }



    @GetMapping("/reset")
    public String forgotPassword(){
        return "resetpassword";
    }

    @PostMapping("/reset")
    public String resetPassword(@RequestParam String email, final HttpServletRequest request, Model model) throws NotFoundException{
        try {
            String resetLink = this.userService.resetPasswordInit(email, applicationUrl(request),"web");
            model.addAttribute("message","Verification has been resent to your email successfully");
            log.info("Click on the following link to reset your password : {}",resetLink);
            return "registration-success";
            
        } catch (Exception e) {
            log.info("Error message : {}", e.getMessage());
            return "error";
        }
        
        
    }




    @GetMapping("/registration-success")
    public String registrationSuccess(Model model){
            return "registration-success";
    }

    @RequestMapping("/resend")
    public String resendVerification(@RequestParam("email") String email, Model model, final HttpServletRequest request) throws NotFoundException{
        try {
            var user = this.userService.checkUser(email);
            String requestType = "web";
            this.publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request),requestType));
            model.addAttribute("email",email);
            return "registration-success";
        }
        catch (Exception e){
            log.info(e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/verifyuser")
    public String verifyUser(@RequestParam("token") String token, Model model){
        try {
            this.userService.validateToken(token);
            return "success-registration";    
        } catch (Exception e) {
            log.info(e.getMessage());
            model.addAttribute("error",e.getMessage());
            return "error";
        }
    }

    @GetMapping("/resetPassword")
    public String resetPass(@RequestParam("token") String token, Model model){
        try {
            String email = this.userService.getUserByToken(token).getEmail();
            if("valid".equalsIgnoreCase(this.userService.validateResetToken(token))){
                model.addAttribute("email",email);
                return "updatepass";
            }
            else {
                return "redirect:/public/login";
            }
        }
        catch (Exception e){
            model.addAttribute("error",e.getMessage());
            return "error";
        }
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam("password")String password, @RequestParam("confirm") String confirm,@RequestParam("email") String email, Model model){
        try {
            if(this.userService.updatePassword(email,password,confirm)){
                return "reset-success";
            };
            return null;
        }
        catch (BadRequestException badRequestException){
            model.addAttribute("error",badRequestException.getMessage());
            return "error";
        }
        catch (Exception e){
            model.addAttribute("error",e.getMessage());
            return "error";
        }
    }

}
