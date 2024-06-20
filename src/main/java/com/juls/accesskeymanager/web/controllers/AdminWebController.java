package com.juls.accesskeymanager.web.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.juls.accesskeymanager.exceptions.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.juls.accesskeymanager.data.models.AccessKeyDetails;
import com.juls.accesskeymanager.services.AccessKeyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


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

//    @PreAuthorize("hasAuthority('ADMIN')")
//    @GetMapping("/dashboard")
//    public String dashboard(Model model) {
//        try {
//            List<AccessKeyDetails> allKeys = this.accessKeyService.getAllAccessKeys();
//            model.addAttribute("keys", allKeys);
//        } catch (Exception e) {
//            model.addAttribute("error", e.getMessage());
//        }
//        return "admin-board";
//    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        try {
            Page<AccessKeyDetails> accessKeyPage = this.accessKeyService.getAllAccessKeyDetails(page, size);
            List<AccessKeyDetails> allKeys = accessKeyPage.getContent();
            int totalPages = accessKeyPage.getTotalPages();

            model.addAttribute("keys", allKeys);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", size);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "admin-board";
    }

    @GetMapping("/sayhello")
    public String sayhello(@RequestParam("email")String email, @RequestParam("keyValue") String keyValue, Model model) throws Exception{
        try{
            accessKeyService.revoke(email,keyValue);
            model.addAttribute("error","Key revoked successfully") ;
            return "error";
        }
        catch (Exception e){
            model.addAttribute("error",e.getMessage());
            return "error";
        }


    }

//    @PreAuthorize("hasAuthority('ADMIN')")
//    @GetMapping("/revoke")
//    public String revokeKey(@RequestParam("email") String email, Model model) throws BadRequestException {
//        System.out.println("This is the email "+email +"...");
//        log.info("This is the email : {}",email);
//            accessKeyService.revokeKey(email);
//        return "redirect:/web/admin/dashboard";
//    }

    @GetMapping("/revokeKey")
    public String revoke(@RequestParam("email") String email, Model model){
        log.info("The email of the user is: {}",email);
        return "redirect:/web/admin/dashoard";
    }

//    @PreAuthorize("hasAuthority('ADMIN')")
//    @PostMapping("/revoke")
//    public ResponseEntity<?> revokeKey(@RequestBody Map<String, String> payload) {
//        String email = payload.get("email");
//        try {
//            this.accessKeyService.revokeKey(email);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/sort")
    @ResponseBody
    public ResponseEntity<?> sortKeys(@RequestParam("sortBy") String sortBy) {
        try {
            List<AccessKeyDetails> sortedKeys = accessKeyService.sortKeys(sortBy);
            return ResponseEntity.ok(sortedKeys);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<?> getActiveKey(@RequestParam("search") String search) {
        try {
            AccessKeyDetails activeKey = accessKeyService.getActiveKeyByEmail(search);
            if (activeKey != null) {
                return ResponseEntity.ok(activeKey);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        }
    }


}