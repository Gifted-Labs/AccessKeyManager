package com.juls.accesskeymanager.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.juls.accesskeymanager.data.models.AccessKeyDetails;
import com.juls.accesskeymanager.data.models.AccessKeys;
import com.juls.accesskeymanager.data.models.Status;
import com.juls.accesskeymanager.data.repository.AccessKeyRepo;
import com.juls.accesskeymanager.exceptions.BadRequestException;

import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.toList;

/**
 * @author Julius Adjetey Sowah
 * @version 1.0
 * The AccessKeyService class contains all the services a user can perform on the application. It comprises all
 * the methods and functions both the users with authority of 'ADMIN' and 'USER' can perform on the application.
 * It makes use of the accessKeyRepo and userService classes to perform it's operations.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessKeyService {

    private final AccessKeyRepo accessKeyRepo;
    private final UserServiceImpl userService;

    
    /**
     * The getAllAccessKeys method is used by the admin to get
     * the list of all the access keys created by users of the application. And
     * it is viewed on the admin's dashboard.
     * @return List<AccessKeyDetails> The list of all the accesskeys created on the application
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


    /**
     * The getAllKeysByEmail method is used to get the list of all keys created by a particular user. This function
     * can be performed by both the user and the admin. It contains all keys, whether they are active, revoked or
     * expired.
     * @apiNote It requires that anyone who wants to access method must have an authority of either 'ADMIN' or 'USER'
     * @param email The email of the user whose keys you want to find
     * @return List</AccessKeyDetails> The list of all the keys generated by that particular user.
     */

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

    /**
     * The getActiveKeyByEmail method is used to get the active key attributed to a particular user. It requires
     * the email of the user who's active key is needed;
     * @apiNote To access this method you need the authority of an 'ADMIN'.
     * @param email The email of the user who the admin want's to retrieve his active key
     * @return activeKey The active key of the user.
     */

    public AccessKeyDetails getActiveKeyByEmail(String email){

        // Get the list of all the keys generated by the user;
        List <AccessKeyDetails> keys = getAllKeysByEmail(email);

        // Loop through all the keys of the user to find the active key.
        AccessKeyDetails activeKey = null;
        for (AccessKeyDetails keyDetails : keys){

            //Check if the key is active and assign it to the active key.
            if (keyDetails.getStatus().equals(Status.ACTIVE)){
                activeKey = keyDetails;
            }
        }
        return activeKey;
    }


    public Optional <AccessKeys> getActiveKeyByStatus(String email){
        Status status = Status.ACTIVE;
        var user = this.userService.getUserByEmail(email);
        return this.accessKeyRepo.findByStatusAndUser(status,user);
    }

    public Page <AccessKeyDetails> getAllAccessKeyDetails(int page, int size){
        Pageable pageable = PageRequest.of(page -1, size);
        Page<AccessKeys> accessKeysPage = accessKeyRepo.findAll(pageable);
        return accessKeysPage.map(this::convertToAccessKeyDetails);
    }

    private AccessKeyDetails convertToAccessKeyDetails(AccessKeys accessKeys){
        AccessKeyDetails keyDetails = new AccessKeyDetails();
        keyDetails.setEmail(accessKeys.getUser().getEmail());
        keyDetails.setKeyValue(accessKeys.getKeyValue());
        keyDetails.setStatus(accessKeys.getStatus());
        keyDetails.setProcured_date(accessKeys.getProcuredDate());
        keyDetails.setExpiry_date(accessKeys.getExpiryDate());

        return keyDetails;
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

    @Transactional
    public AccessKeys revokeKey(String email){
        try{
            log.info("The error for the key {}",email);
            // Get the key of the user.
            var accessKey = this.accessKeyRepo.findByStatusAndUser(Status.ACTIVE,this.userService.getUserByEmail(email));
            log.info("This is the key value {}",accessKey.get().getKeyValue());
            accessKey.get().setStatus(Status.REVOKED);
            return this.accessKeyRepo.save(accessKey.get());
        }
        catch (Exception e){
            log.info(e.getMessage());
            return null;
        }
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
