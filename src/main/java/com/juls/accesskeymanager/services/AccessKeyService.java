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
            detials.setEmail(keys.getUser().getEmail());
            keyDetails.put(keys.getKeyId(), detials);
        });

        return keyDetails.values().stream().toList();
    }

    public List <AccessKeyDetails> getAllKeysByEmail(String email){
        List <AccessKeys> accessKeys = this.accessKeyRepo.findByUser(this.userService.getUserByEmail(email));
        Map <Long, AccessKeyDetails> keyDetails = new HashMap<>();
        accessKeys.forEach(keys -> {
            AccessKeyDetails detials = new AccessKeyDetails();
            detials.setKeyValue(keys.getKeyValue());
            detials.setProcured_date(keys.getProcuredDate());
            detials.setExpiry_date(keys.getExpiryDate());
            detials.setStatus(keys.getStatus());
            detials.setEmail(keys.getUser().getEmail());
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


    /**
     * This method revokes the access key of the user who's email has been selected
     * @param email The email of the user 
     * @return It saves the modified access key in the repository and returns the new one.
     */
    public AccessKeys revokeKey(String email){
        AccessKeyDetails key = getActiveKeyByEmail(email);
        Optional <AccessKeys> accessKey = this.accessKeyRepo.findByKeyValue(key.getKeyValue());
        accessKey.get().setStatus(Status.REVOKED);
        return this.accessKeyRepo.save(accessKey.get());
    }

    /**
     * This method is used by the user to generate or request for a new access key. It first checks 
     * whether the user has no active access key before it generates a new one for the user.
     * @param email It takes a parameter of the email the user who wants to generate a new key.
     * @return It returns a new generated access key for the user.
     */
    private AccessKeys generatAccessKeys(String email){
        Date currentDate = new Date(System.currentTimeMillis());
        AccessKeys accessKey = new AccessKeys();
        accessKey.setKeyValue(keyValue());
        accessKey.setProcuredDate(currentDate);
        accessKey.setExpiryDate(calculateExpiryDate(currentDate));
        accessKey.setStatus(Status.ACTIVE);
        accessKey.setUser(this.userService.getUserByEmail(email));
        return accessKey;
    }

    public AccessKeys generateKey(String email) throws BadRequestException{
        AccessKeys key = new AccessKeys();
        if (this.getActiveKeyByEmail(email)==null){
            key = generatAccessKeys(email);
        }
        else{
            throw new BadRequestException("You already have an active key");
        }
    
        return this.accessKeyRepo.save(key);
    }

    private String keyValue (){
        String values = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrsquvwxyz1234567890";
        Random rand = new Random();
        StringBuilder generatedValue = new StringBuilder("");
         for (int i = 0; i < 10; i++){
            generatedValue.append(values.charAt(rand.nextInt(values.length())));
         }
         return generatedValue.toString();
    }

    private Date calculateExpiryDate(Date procuredDate){
        LocalDate currDate = procuredDate.toLocalDate();
        LocalDate expiryDate = currDate.plusDays(1);
        return Date.valueOf(expiryDate);
    }

    public void checkAndHandleExpiry(){
        List <AccessKeys> accessKeyList = this.accessKeyRepo.findAll();
        for (AccessKeys key : accessKeyList){
            Date currDate = new Date(System.currentTimeMillis());
            if (!key.getStatus().equals(Status.REVOKED)){
                if (key.getExpiryDate().getTime()<= currDate.getTime()){
                    key.setStatus(Status.EXPIRED);
                    this.accessKeyRepo.save(key);
                }
            }
        }

    }


    
}
