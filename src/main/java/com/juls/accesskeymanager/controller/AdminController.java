package com.juls.accesskeymanager.controller;

import org.springframework.security.access.prepost.PreAuthorize;
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
}
