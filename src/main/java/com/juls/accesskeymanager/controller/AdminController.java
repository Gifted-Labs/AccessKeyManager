package com.juls.accesskeymanager.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.juls.accesskeymanager.services.AccessKeyService;

import lombok.extern.slf4j.Slf4j;

import com.juls.accesskeymanager.data.models.AccessKeyDetails;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AccessKeyService accessKeyService;

    @GetMapping("/dashboard")
    public ResponseEntity<List <AccessKeyDetails>> dashboard(){
            List <AccessKeyDetails> keyDetails = this.accessKeyService.getAllAccessKeys();
            return ResponseEntity.ok(keyDetails);
            
        
    }

    @GetMapping("/userkeys")
    public ResponseEntity<List <AccessKeyDetails>> userKeys(@RequestParam("email") String email){
        List <AccessKeyDetails> keys = this.accessKeyService.getAllKeysByEmail(email);
        return ResponseEntity.ok(keys);
    }

    @GetMapping("/revoke")
    public ResponseEntity<List <AccessKeyDetails>> revokeAccessKey(@RequestParam(value="email") String email){
        try{
            this.accessKeyService.revokeKey(email);
        }
        catch(Exception e){
            log.info(e.getMessage());
        }
        List <AccessKeyDetails> keys = this.accessKeyService.getAllAccessKeys();
        return ResponseEntity.ok(keys);
    }


    @GetMapping("/activekey")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<AccessKeyDetails> findActiveKey(@RequestParam(value="email", required = false) String email){
        AccessKeyDetails accessKey = this.accessKeyService.getActiveKeyByEmail(email); 
        return ResponseEntity.ok(accessKey);
        }
}
