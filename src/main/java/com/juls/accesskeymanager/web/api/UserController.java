package com.juls.accesskeymanager.web.api;

import com.juls.accesskeymanager.data.models.AccessKeys;
import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.exceptions.NotFoundException;
import com.juls.accesskeymanager.services.AccessKeyDetails;
import com.juls.accesskeymanager.services.AccessKeyService;
import com.juls.accesskeymanager.services.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
        List <AccessKeyDetails> allKeys = this.accessKeyService.getAllAccessKeys();
        return allKeys;
    }

    @GetMapping("/email")
    public List<AccessKeyDetails> getAccessKeysByEmail(@RequestParam(value="email") String email){
        return this.accessKeyService.getAllKeysByEmail(email);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AccessKeyDetails getActiveAccessKey(@RequestParam(value="email") String email){
        AccessKeyDetails accessKey = this.accessKeyService.getActiveKeyByEmail(email);
        return accessKey;
    }

    @GetMapping("/revoke")
    public AccessKeys revokeAccessKey(@RequestParam(value="email") String email){
        AccessKeys keyDetails = this.accessKeyService.revokeKey(email);
        return keyDetails;
    }

}
