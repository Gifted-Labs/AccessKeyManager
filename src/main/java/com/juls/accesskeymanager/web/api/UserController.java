package com.juls.accesskeymanager.web.api;

import com.juls.accesskeymanager.data.models.AccessKeyDetails;
import com.juls.accesskeymanager.data.models.AccessKeys;
import com.juls.accesskeymanager.exceptions.BadRequestException;
import com.juls.accesskeymanager.services.AccessKeyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {


    private final AccessKeyService accessKeyService;



    @GetMapping("/all")
    public ResponseEntity<List<AccessKeyDetails>> getAllKeys(Authentication authentication){
        List <AccessKeyDetails> allKeys = this.accessKeyService.getAllKeysByEmail(authentication.getName());
        return ResponseEntity.ok(allKeys);
    }
   
   @GetMapping("/generate")
    public ResponseEntity<Void> generateKey(Authentication authentication) throws BadRequestException{
        try {
            String email = authentication.getName();
            this.accessKeyService.generateKey(email);
            HttpHeaders headers = new HttpHeaders();
            URI uri = new URI("/users/all");
            headers.setLocation(uri);
            return new ResponseEntity<>(headers,HttpStatus.CONTINUE);   
        } catch (Exception e) {
            log.error(e.getMessage());            
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

}
