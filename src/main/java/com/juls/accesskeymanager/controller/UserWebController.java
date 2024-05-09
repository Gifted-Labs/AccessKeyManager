package com.juls.accesskeymanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.juls.accesskeymanager.data.models.AccessKeys;
import com.juls.accesskeymanager.exceptions.BadRequestException;
import com.juls.accesskeymanager.services.AccessKeyDetails;
import com.juls.accesskeymanager.services.AccessKeyService;

@Controller
@RequestMapping("/web")
public class UserWebController {
    
    @Autowired
    private AccessKeyService accessKeyService;
    
    @GetMapping("/all")
    public String dashboard(Model model){
        List <AccessKeyDetails> keyDetails = this.accessKeyService.getAllAccessKeys();
        model.addAttribute("keys", keyDetails);
        return "user-dashboard";
    }

    @GetMapping("/generate")
    public String generateKey() throws BadRequestException{
        String email = "julius@yahoo.com";
        AccessKeys accessKey = this.accessKeyService.generateKey(email);
        return "redirect:/web/all";
    }


    @GetMapping("/search")
    public String findActiveKey(@RequestParam(value="email", required = false) String email,Model model){
        AccessKeyDetails accessKey = this.accessKeyService.getActiveKeyByEmail(email);
         model.addAttribute("key", accessKey);
         return "admin-board";
        }

}
