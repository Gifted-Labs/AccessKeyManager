package com.juls.accesskeymanager.data.events.listeners;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import com.juls.accesskeymanager.data.events.SendEmailEvent;
import com.juls.accesskeymanager.services.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SendEmailEventListener implements ApplicationListener<SendEmailEvent> {

    @Autowired
    private  EmailService emailService;

    @Override
    public void onApplicationEvent(SendEmailEvent event){
        
        if (event.reason.equalsIgnoreCase("verification")){
            this.emailService.sendVerificationEmail(event.getRequest());
        }
        else if(event.reason.equalsIgnoreCase("reset")){
            this.emailService.sendResetTokenEmail(event.getRequest());
        }
        else if(event.reason.equalsIgnoreCase("successReset")){
            this.emailService.sendResetSuccessEmail(event.getRequest().getReciepient());
        }
        else if(event.reason.equalsIgnoreCase("successRegister")){
            this.emailService.sendVerificationSuccessEmail(event.getRequest().getReciepient());       
        
    }
    log.info("Email Successfully Sent");
}
}