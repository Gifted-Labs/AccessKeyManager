package com.juls.accesskeymanager.data.events;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailEvent extends ApplicationEvent {
    private String reciepient;
    private String request;

    public SendEmailEvent(String reciepient, String request){
        super(reciepient);

        this.reciepient = reciepient;
        this.request = request;

    }
    
}
