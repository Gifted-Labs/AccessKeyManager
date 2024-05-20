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


    public RegistrationCompleteEvent(Users user, String applicationUrl){
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;

    }
}
