package com.juls.accesskeymanager.controller;

<<<<<<< HEAD
<<<<<<< HEAD
=======
import org.springframework.security.access.prepost.PreAuthorize;
>>>>>>> temp
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin")
public class AdminController {
    


    @GetMapping("/api")
    @PreAuthorize("hasRole('ADMIN')")
    public String getUsers(){
        return "This are all the users we have";
    }
=======


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.juls.accesskeymanager.services.AccessKeyService;
import com.juls.accesskeymanager.data.models.AccessKeys;
import com.juls.accesskeymanager.exceptions.BadRequestException;
import com.juls.accesskeymanager.services.AccessKeyDetails;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AccessKeyService accessKeyService;

    @GetMapping("dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String dashboard(Model model){
        List <AccessKeyDetails> keyDetails = this.accessKeyService.getAllAccessKeys();
        model.addAttribute("keys", keyDetails);
        return "user-dashboard";
    }

    @GetMapping("/generate")
    public String generateKey() throws BadRequestException{
        String email = "julius@yahoo.com";
        AccessKeys accessKey = this.accessKeyService.generateKey(email);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/search")
    public String findActiveKey(@RequestParam(value="email", required = false) String email,Model model){
        AccessKeyDetails accessKey = this.accessKeyService.getActiveKeyByEmail(email);
         model.addAttribute("key", accessKey);
         return "admin-board";
        }
>>>>>>> parent of 66409c8 (I implemented the authentication method, specifically the user registration and also modified the user table to include the is_enabled field and also created a table for the verificaiton token et all)
}
