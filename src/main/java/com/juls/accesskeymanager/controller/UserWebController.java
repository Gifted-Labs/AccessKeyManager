package com.juls.accesskeymanager.controller;



import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juls.accesskeymanager.data.models.AuthenticationRequest;
import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.services.UserServiceImpl;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class UserWebController {
    
    private final UserServiceImpl userService;

    @PostMapping
        public String registerUser(@RequestBody AuthenticationRequest authenticationRequest){
            Users user = this.userService.registerUser(authenticationRequest);
            return "User registered successfully";
        }


}
