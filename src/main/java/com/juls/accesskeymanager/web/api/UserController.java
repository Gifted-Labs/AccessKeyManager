package com.juls.accesskeymanager.web.api;

import com.juls.accesskeymanager.data.models.AccessKeyDetails;
import com.juls.accesskeymanager.data.models.AccessKeys;
import com.juls.accesskeymanager.exceptions.BadRequestException;
import com.juls.accesskeymanager.services.AccessKeyService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {


    private final AccessKeyService accessKeyService;



    @GetMapping("/all")
    public List<AccessKeyDetails> getAllKeys(Authentication authentication){
        List <AccessKeyDetails> allKeys = this.accessKeyService.getAllKeysByEmail(authentication.getName());
        return allKeys;
    }




   
   @GetMapping("/generate")
    public List <AccessKeyDetails> generateKey(Authentication authentication) throws BadRequestException{
        String email = authentication.getName();
        AccessKeys key = this.accessKeyService.generateKey(email);
        return this.accessKeyService.getAllKeysByEmail(email);
    }

}
