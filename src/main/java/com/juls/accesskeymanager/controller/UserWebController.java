package com.juls.accesskeymanager.controller;

import java.util.List;

<<<<<<< HEAD

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juls.accesskeymanager.data.events.RegistrationCompleteEvent;
import com.juls.accesskeymanager.data.models.AuthenticationRequest;
import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.data.token.VerificationToken;
import com.juls.accesskeymanager.services.UserServiceImpl;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class UserWebController {
    
    private final UserServiceImpl userService;
    private final ApplicationEventPublisher publisher;

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
=======
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.juls.accesskeymanager.data.models.AccessKeys;
import com.juls.accesskeymanager.exceptions.BadRequestException;
import com.juls.accesskeymanager.services.AccessKeyDetails;
import com.juls.accesskeymanager.services.AccessKeyService;

@Controller
@RequestMapping()
public class UserWebController {
    

    @GetMapping("/login")
        public String login(){
            return "login";
>>>>>>> parent of 66409c8 (I implemented the authentication method, specifically the user registration and also modified the user table to include the is_enabled field and also created a table for the verificaiton token et all)
        }

        String verificationResult = this.userService.validateToken(token);
        if (verificationResult.equalsIgnoreCase("valid")){
            return "Email verification successful. Please Login";
        }

        return "Verification Failed";

    }
    
    @GetMapping("/checkman")
    public String checkSomething(){
        return "I am checking something";
    }


}
