package com.juls.accesskeymanager.web.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.juls.accesskeymanager.data.models.AccessKeyDetails;
import com.juls.accesskeymanager.services.AccessKeyService;
import com.juls.accesskeymanager.services.UserServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/web/users")
@RequiredArgsConstructor
public class UserWebController  {
    
    private final AccessKeyService accessKeyService;
    

    @GetMapping
    public String allKeys(Authentication authentication, Model model){
        try {
            String email = authentication.getName();
            List <AccessKeyDetails> keyList = this.accessKeyService.getAllKeysByEmail(email);
            model.addAttribute("email", email);
            model.addAttribute("keys", keyList);
            return "user-dashboard";            
        } catch (Exception e) {
            log.info("The error message is : {}",e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }


    @PostMapping("/generate")
    public String generate(Authentication authentication){
        try {
            String email = authentication.getName();
            this.accessKeyService.generateKey(email);
            return "redirect:/web/users";
        } catch (Exception e) {
            log.debug("Error info: {}",e.getMessage());
            return "redirect:/web/users?error="+e.getMessage();
        }
    }
    
}
