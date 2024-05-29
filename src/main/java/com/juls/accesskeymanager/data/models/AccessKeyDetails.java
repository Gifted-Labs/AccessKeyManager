package com.juls.accesskeymanager.data.models;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public class AccessKeyDetails {

    private String keyValue;
    private Date procured_date;
    private Date expiry_date;
    private Status status;
    private String email;


    // public AccessKeyDetails (AccessKeys keys){
    //     this.keyValue = keys.getKeyValue();
    //     this.procured_date =  keys.getProcuredDate();
    //     this.expiry_date = keys.getExpiryDate();
    //     this.email = keys.getUser().getEmail();
    // }
    
}
