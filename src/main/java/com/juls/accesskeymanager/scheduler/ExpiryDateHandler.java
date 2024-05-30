package com.juls.accesskeymanager.scheduler;

import java.util.Calendar;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.juls.accesskeymanager.data.models.AccessKeys;
import com.juls.accesskeymanager.data.models.Status;
import com.juls.accesskeymanager.data.repository.VerificationTokenRepository;
import com.juls.accesskeymanager.data.token.VerificationToken;
import com.juls.accesskeymanager.services.AccessKeyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiryDateHandler {
    
    private final AccessKeyService accessKeyService;
    private final VerificationTokenRepository verificationTokenRepository;

    @Scheduled(fixedRate = 120000)
    public void checkForExpiredKeys(){
        List <AccessKeys> allKeys = this.accessKeyService.getAllKeys();
        for (AccessKeys key : allKeys){
            Calendar calendar = Calendar.getInstance();
            if (key.getStatus()!=Status.REVOKED){

                if (key.getExpiryDate().getTime() - calendar.getTime().getTime() <= 0){
                    key.setStatus(Status.EXPIRED);
                    this.accessKeyService.saveAccessKey(key);
                }
            }
        }
        log.info("All expired keys updated");
    }

    @Scheduled(fixedRate=900000)
    public void checkForExpiredTokens(){
        List <VerificationToken> verificationTokens = this.verificationTokenRepository.findAll();
        verificationTokens.forEach(token -> {
            Calendar calendar = Calendar.getInstance();
            if(token.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0){
                this.verificationTokenRepository.delete(token);
            }
        });
        log.info("All Expired tokens deleted");
    }
}
