package com.juls.accesskeymanager.web.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.juls.accesskeymanager.data.models.AccessKeyDetails;
import com.juls.accesskeymanager.services.AccessKeyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author GiftedLabs
 * @version 1.0
 * @since 2024
 *
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/web/admin")
public class AdminWebController {
    
    private final AccessKeyService accessKeyService;

    @GetMapping("/dashboard")
    public String dashboard(Model model){
        try {
            List <AccessKeyDetails> allKeys =  this.accessKeyService.getAllAccessKeys();
            model.addAttribute("keys", allKeys);
            return "admin-board";
        } catch (Exception e) {
            log.debug("Error message: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/revoke")
    public String revokeKey(@RequestParam("email") String email, Model model){
        try {
            log.info("Revoking a key");
            this.accessKeyService.revokeKey(email);
            log.info("Key revoked successfully");
            return "redirect:/web/admin/dashboard";
        } catch (Exception e) {
            log.info("Error message: {}",e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }



    @GetMapping("/search")
    @ResponseBody
    public AccessKeyDetails getActiveKey(@RequestParam("search") String search, Model model){
        return accessKeyService.getActiveKeyByEmail(search);}

    }
