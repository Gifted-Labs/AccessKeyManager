package com.juls.accesskeymanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

import java.util.List;

import com.juls.accesskeymanager.data.models.AccessKeyDetails;
import com.juls.accesskeymanager.services.AccessKeyService;


public class CLRunner implements CommandLineRunner {

    private AccessKeyService accessKeyService;

    private final String email = "reynolds@gmail.com";

    
    /** 
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception{


        List <AccessKeyDetails> keys = this.accessKeyService.getAllKeysByEmail(email);

        keys.forEach(System.out::println);

        
        System.out.println("ACTIVE KEYS");
        System.out.println("==============");
        System.out.println(this.accessKeyService.getActiveKeyByEmail(email));
        

        
        List <AccessKeyDetails> keyDetails = this.accessKeyService.getAllAccessKeys();

//        this.accessKeyService.revokeKey(email);
        System.out.println("AFTER REVOKING");
        System.out.println("================");
        keyDetails.forEach(System.out::println);

        System.out.println();
        System.out.println("GENERATING NEW KEY");
        System.out.println(this.accessKeyService.generateKey(email));
    }
    
}
