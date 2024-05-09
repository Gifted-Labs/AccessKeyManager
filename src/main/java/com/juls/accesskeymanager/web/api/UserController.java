package com.juls.accesskeymanager.web.api;

import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.services.AccessKeyDetails;
import com.juls.accesskeymanager.services.AccessKeyService;
import com.juls.accesskeymanager.services.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/accesskeys")
public class UserController {

    @Autowired
    private AccessKeyService accessKeyService;

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/all")
    public List<AccessKeyDetails> getAllKeys(){
        return this.accessKeyService.getAllAccessKeys();
    }

    @GetMapping("/{email}")
    public List <AccessKeyDetails> getAccessKeysByEmail(@PathVariable("email") String email){
        return this.accessKeyService.getAllKeysByEmail(email);
    }

}
