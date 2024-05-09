package com.juls.accesskeymanager.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.juls.accesskeymanager.data.models.AccessKeys;
import com.juls.accesskeymanager.data.models.Status;
import com.juls.accesskeymanager.data.repository.AccessKeyRepo;
import com.juls.accesskeymanager.exceptions.BadRequestException;

@Service
public class AccessKeyService {

    @Autowired
    private AccessKeyRepo accessKeyRepo;
    
    @Autowired
    private UserServiceImpl userService;

    public List<AccessKeyDetails> getAllAccessKeys(){
        List <AccessKeys> accessKeys = this.accessKeyRepo.findAll();
        Map <Long, AccessKeyDetails> keyDetails = new HashMap<>();
        accessKeys.forEach(keys -> {
            AccessKeyDetails detials = new AccessKeyDetails();
            detials.setKeyValue(keys.getKeyValue());
            detials.setProcured_date(keys.getProcuredDate());
            detials.setExpiry_date(keys.getExpiryDate());
            detials.setStatus(keys.getStatus());
            detials.setEmail(this.userService.getUserEmailById(keys.getUserId()));
            keyDetails.put(keys.getKeyId(), detials);
        });

        return keyDetails.values().stream().toList();
    }

    public List <AccessKeyDetails> getAllKeysByEmail(String email){
        List <AccessKeys> accessKeys = this.accessKeyRepo.findByUserId(this.userService.getUserIdByEmail(email));
        Map <Long, AccessKeyDetails> keyDetails = new HashMap<>();
        accessKeys.forEach(keys -> {
            AccessKeyDetails detials = new AccessKeyDetails();
            detials.setKeyValue(keys.getKeyValue());
            detials.setProcured_date(keys.getProcuredDate());
            detials.setExpiry_date(keys.getExpiryDate());
            detials.setStatus(keys.getStatus());
            detials.setEmail(this.userService.getUserEmailById(keys.getUserId()));
            keyDetails.put(keys.getKeyId(), detials);
        });

        return keyDetails.values().stream().toList();
    }

    public AccessKeyDetails getActiveKeyByEmail(String email){
        List <AccessKeyDetails> keys = getAllKeysByEmail(email);
        AccessKeyDetails activeKey = null;
        for (AccessKeyDetails keyDetails : keys){
            if (keyDetails.getStatus().equals(Status.ACTIVE)){
                activeKey = keyDetails;
            }
        }
        return activeKey;
    }

    public AccessKeys revokeKey(String email){
        AccessKeyDetails key = getActiveKeyByEmail(email);
        Optional <AccessKeys> accessKey = this.accessKeyRepo.findByKeyValue(key.getKeyValue());
        accessKey.get().setStatus(Status.REVOKED);
        return this.accessKeyRepo.save(accessKey.get());
    }

    private AccessKeys generatAccessKeys(String email){
        Date currentDate = new Date(System.currentTimeMillis());
        AccessKeys accessKey = new AccessKeys();
        accessKey.setKeyValue(keyValue());
        accessKey.setProcuredDate(currentDate);
        accessKey.setExpiryDate(calculateExpiryDate(currentDate));
        accessKey.setStatus(Status.ACTIVE);
        accessKey.setUserId(this.userService.getUserIdByEmail(email));
        return accessKey;
    }

    public AccessKeys generateKey(String email){
        AccessKeys key = new AccessKeys();
        if (this.getActiveKeyByEmail(email)==null){
            key = generatAccessKeys(email);
        }
    
        return this.accessKeyRepo.save(key);
    }

    private String keyValue (){
        String values = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrsquvwxyz1234567890";
        Random rand = new Random();
        StringBuilder generatedValue = new StringBuilder();
         for (int i = 0; i < 10; i++){
            generatedValue.append(values.charAt(rand.nextInt(values.length())));
         }
         return generatedValue.toString();
    }

    private Date calculateExpiryDate(Date procuredDate){
        LocalDate currDate = procuredDate.toLocalDate();
        LocalDate expiryDate = currDate.plusYears(1);
        return Date.valueOf(expiryDate);
    }


    
}
