package com.juls.accesskeymanager.data.events.listeners;

import java.util.UUID;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.juls.accesskeymanager.data.events.RegistrationCompleteEvent;
import com.juls.accesskeymanager.data.models.EmailRequest;
import com.juls.accesskeymanager.services.EmailService;
import com.juls.accesskeymanager.services.UserServiceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final UserServiceImpl userService;
    private final EmailService emailService;



    
    /** 
     * @param event
     */
    @Override
    @Transactional 
    public void onApplicationEvent(@SuppressWarnings("null") RegistrationCompleteEvent event){
            
        // 1. Get the newly registered user
        var user = event.getUser();


        // 2. Create a verification token for the user.
        String verificationToken = UUID.randomUUID().toString();


        //3. Save the verification token for the user.
        this.userService.saveVerificationToken(user, verificationToken );

        //4. Build the applicationUrl;
        String url = event.getApplicationUrl()+"/register/verifyuser?token="+verificationToken;


        // 5. Send the email;
        log.info("Click on the link to veriy your account : {}",url);
        EmailRequest request = new EmailRequest(user.getEmail(),url);
        emailService.sendVerificationEmail(request);
    }
    
}
