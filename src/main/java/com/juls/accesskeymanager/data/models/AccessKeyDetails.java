package com.juls.accesskeymanager.data.models;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents details of an access key.
 * This class encapsulates information related to an access key, including its key value,
 * procurement date, expiry date, status, and associated email.
 */

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
