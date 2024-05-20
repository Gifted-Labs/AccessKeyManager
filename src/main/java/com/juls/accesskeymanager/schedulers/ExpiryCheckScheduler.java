package com.juls.accesskeymanager.schedulers;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.juls.accesskeymanager.services.AccessKeyService;

@Component
public class ExpiryCheckScheduler {
    
    @Autowired
    private  AccessKeyService accessKeyService;

    @Scheduled(fixedRate=180000)      // Runs every 24 hours.
    public void checkEntry(){
        this.accessKeyService.checkAndHandleExpiry();

    }


}
