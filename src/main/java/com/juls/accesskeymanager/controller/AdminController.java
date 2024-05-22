package com.juls.accesskeymanager.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.juls.accesskeymanager.services.AccessKeyService;
import com.juls.accesskeymanager.data.models.AccessKeyDetails;
import com.juls.accesskeymanager.data.models.AccessKeys;
import com.juls.accesskeymanager.exceptions.BadRequestException;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AccessKeyService accessKeyService;

    @GetMapping("/dashboard")
    public List <AccessKeyDetails> dashboard(){
        List <AccessKeyDetails> keyDetails = this.accessKeyService.getAllAccessKeys();
        return keyDetails;
    }

    @GetMapping("/userkeys")
    public List <AccessKeyDetails> userKeys(@RequestParam("email") String email){
        return this.accessKeyService.getAllKeysByEmail(email);
    }

    @GetMapping("/revoke")
    public AccessKeys revokeAccessKey(@RequestParam(value="email") String email){
        return this.accessKeyService.revokeKey(email);
    }


    @GetMapping("/activekey")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AccessKeyDetails findActiveKey(@RequestParam(value="email", required = false) String email){
        AccessKeyDetails accessKey = this.accessKeyService.getActiveKeyByEmail(email);
         return accessKey;
        }
}
