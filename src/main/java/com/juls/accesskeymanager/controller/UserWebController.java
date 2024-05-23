package com.juls.accesskeymanager.controller;


import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juls.accesskeymanager.data.events.RegistrationCompleteEvent;
import com.juls.accesskeymanager.data.models.AuthenticationRequest;
import com.juls.accesskeymanager.data.models.Password;
import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.data.token.VerificationToken;
import com.juls.accesskeymanager.exceptions.NotFoundException;
import com.juls.accesskeymanager.services.UserServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class UserWebController {
    
    private final UserServiceImpl userService;
    private final ApplicationEventPublisher publisher;
    private String email;

    @PostMapping
    public String registerUser(@RequestBody AuthenticationRequest authenticationRequest, final HttpServletRequest request){
        Users user = this.userService.registerUser(authenticationRequest);
        this.publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "User registered successfully";
    }

    private String applicationUrl(HttpServletRequest request){
        String url = String.format("http://%s:%s%s",request.getServerName(),request.getServerPort(),request.getContextPath());
        return url;
    }

    @PostMapping("/verifyEmail")
    public String verifyEmail(@RequestParam(value = "token") String token){
        VerificationToken verificationToken = this.userService.findToken(token);
        if(verificationToken.getUser().isEnabled()){
            return "User is already verified! Please login";
        }

        String verificationResult = this.userService.validateToken(token);
        if (verificationResult.equalsIgnoreCase("valid")){
            return "Email verification successful. Please Login";
        }

        return "Verification Failed";

    }

    @PostMapping("/reset")
    public String resetInit(@RequestParam(value = "email")String email, final HttpServletRequest request) throws NotFoundException{
        return this.userService.resetPasswordInit(email, applicationUrl(request));
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam(value="token") String token){
        this.email = this.userService.getUserByEmail(token).getEmail();
        if (this.userService.validateResetToken(token).equalsIgnoreCase("valid")){
            return "redirect:/reset/update";
        }
        return null;
    }

    @PostMapping("/update")
    public String updatePassword(){
        return "Password updated successfully";
    }
    
    




    @GetMapping("/checkman")
    public String checkSomething(){
        return "I am checking something";
    }


}
