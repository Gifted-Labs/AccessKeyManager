package com.juls.accesskeymanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

import com.juls.accesskeymanager.services.AccessKeyDetails;
import com.juls.accesskeymanager.services.AccessKeyService;



public class CLRunner implements CommandLineRunner {

    @Autowired
    private AccessKeyService accessKeyService;



    @Override
    @Scheduled(fixedRate=60000)
    public void run(String... args) throws Exception{


        List <AccessKeyDetails> keys = this.accessKeyService.getAllAccessKeys();

        keys.forEach(System.out::println);

        
        System.out.println("RUNNING SCHEDULER");
        System.out.println("==============");
        this.accessKeyService.checkAndHandleExpiry();
    
        

        
        List <AccessKeyDetails> keyDetails = this.accessKeyService.getAllAccessKeys();

        System.out.println("AFTER RUNNING");
        System.out.println("================");
        keyDetails.forEach(System.out::println);

    }
    
}
