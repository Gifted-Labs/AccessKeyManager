package com.juls.accesskeymanager.data.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class EmailRequest {
    
    private String reciepient;
    private String url;
}
