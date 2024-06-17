package com.juls.accesskeymanager.data.events.listeners;


import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import com.juls.accesskeymanager.data.events.SendEmailEvent;
import com.juls.accesskeymanager.services.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SendEmailEventListener implements ApplicationListener<SendEmailEvent> {

    private final EmailService emailService;

    
    /** 
     * @param event
     */
    @Override
    public void onApplicationEvent(SendEmailEvent event){

        log.info("I am coming to send a message from the event listener");
        
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