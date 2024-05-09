package com.juls.accesskeymanager.services;

import com.juls.accesskeymanager.data.models.Status;

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


    
}
