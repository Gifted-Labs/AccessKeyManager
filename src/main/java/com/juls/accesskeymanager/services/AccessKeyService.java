package com.juls.accesskeymanager.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.juls.accesskeymanager.data.models.AccessKeyDetails;
import com.juls.accesskeymanager.data.models.AccessKeys;
import com.juls.accesskeymanager.data.models.Status;
import com.juls.accesskeymanager.data.repository.AccessKeyRepo;
import com.juls.accesskeymanager.exceptions.BadRequestException;

import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class AccessKeyService {

    private final AccessKeyRepo accessKeyRepo;
    private final UserServiceImpl userService;

    
    /** 
     * @return List<AccessKeyDetails>
     */
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





//    public AccessKeys revokeKey(String email) throws BadRequestException{
//        AccessKeyDetails key = getActiveKeyByEmail(email);
//        Optional <AccessKeys> accessKey = this.accessKeyRepo.findByKeyValue(key.getKeyValue());
//        if (accessKey.get().getStatus()==Status.EXPIRED || accessKey.get().getStatus()==Status.REVOKED){
//            throw new BadRequestException("You cannot revoke this key");
//        }
//        accessKey.get().setStatus(Status.REVOKED);
//        AccessKeys updatedKey = this.accessKeyRepo.save(accessKey.get());
//        return updatedKey;
//    }

    public Optional <AccessKeys> getActiveKeyByStatus(String email){
        Status status = Status.ACTIVE;
        var user = this.userService.getUserByEmail(email);
        return this.accessKeyRepo.findByStatusAndUser(status,user);
    }

    public List<AccessKeyDetails> sortKeys(String sortBy) {
        List<AccessKeyDetails> allKeys = new ArrayList<>(getAllAccessKeys());

        switch (sortBy.toLowerCase()) {
            case "email":
                allKeys.sort(Comparator.comparing(AccessKeyDetails::getEmail));
                break;
            case "expirydate":
                allKeys.sort(Comparator.comparing(AccessKeyDetails::getExpiry_date));
                break;
            case "status":
                allKeys.sort(Comparator.comparing(AccessKeyDetails::getStatus));
                break;
            default:
                break;
        }

        return allKeys;
    }



    public AccessKeys revokeKey(String email) throws BadRequestException {
        Optional<AccessKeys> accessKey = this.getActiveKeyByStatus(email);

        if (accessKey.isPresent()) {
            if (accessKey.get().getStatus() == Status.EXPIRED || accessKey.get().getStatus() == Status.REVOKED) {
                throw new BadRequestException("You cannot revoke this key");
            }

        }
        else if(accessKey.isEmpty()){
            throw new BadRequestException("No Access Key found");
        }
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
        accessKey.setUser(this.userService.getUserByEmail(email));
        return accessKey;
    }

    public boolean userHasActiveKey(String email){
        return getActiveKeyByEmail(email)!=null;
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AccessKeys generateKey(String email) throws BadRequestException {
        // Check if user already has an active key
        if (userHasActiveKey(email)) {
            throw new BadRequestException("You already have an active key");
        }

        // Generate and save a new access key
        AccessKeys key = generatAccessKeys(email);
        return this.accessKeyRepo.save(key);
    }

    private String keyValue (){
        String values = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrsquvwxyz1234567890";
        Random rand = new Random();
        StringBuilder generatedValue = new StringBuilder();
         for (int i = 0; i < 32; i++){
            generatedValue.append(values.charAt(rand.nextInt(values.length())));
         }
         return generatedValue.toString();
    }

    private Date calculateExpiryDate(Date procuredDate){
        LocalDate currDate = procuredDate.toLocalDate();
        LocalDate expiryDate = currDate.plusDays(1);
        return Date.valueOf(expiryDate);
    }

    public List <AccessKeys> getAllKeys(){
        return this.accessKeyRepo.findAll();
    }

    public AccessKeys saveAccessKey(AccessKeys key){
        return this.accessKeyRepo.save(key);
    }


}
