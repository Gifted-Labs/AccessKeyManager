package com.juls.accesskeymanager.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juls.accesskeymanager.data.models.AccessKeyDetails;
import com.juls.accesskeymanager.data.models.AccessKeys;
import com.juls.accesskeymanager.exceptions.BadRequestException;
import com.juls.accesskeymanager.services.AccessKeyService;
import com.juls.accesskeymanager.services.UserServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    
    private final UserServiceImpl userService;
    private final AccessKeyService accessKeyService;
    
    @GetMapping("/generate")
    public List <AccessKeyDetails> generate() throws BadRequestException{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = getEmailFromAuthentication(authentication);
        AccessKeys key = this.accessKeyService.generateKey(email);
        return accessKeyService.getAllKeysByEmail(email);
    
    }

    private String getEmailFromAuthentication(Authentication authentication){
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails){
            UserDetails userDetails =   (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }

        return null;
        
    }
}
