package com.juls.accesskeymanager.data.events;

import org.springframework.context.ApplicationEvent;

import com.juls.accesskeymanager.data.models.Users;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationCompleteEvent extends ApplicationEvent{
    
    private Users user;
    private String applicationUrl;
    private String requestType;


    public RegistrationCompleteEvent(Users user, String applicationUrl,String requestType){
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
        this.requestType = requestType;

    }
}
