package com.juls.accesskeymanager.data.events;


import org.springframework.context.ApplicationEvent;

import com.juls.accesskeymanager.data.models.EmailRequest;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailEvent extends ApplicationEvent {
    private EmailRequest request;
    public String reason;

    public SendEmailEvent(EmailRequest request, String reason){
        super(request);
        this.request = request;
        this.reason = reason;

        }
    }
    

