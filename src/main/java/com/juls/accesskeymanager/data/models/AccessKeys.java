package com.juls.accesskeymanager.data.models;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data


@Entity
@Table(name = "accesskeys")
public class AccessKeys {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    private long keyId;

    @Column(name = "key_value")
    private String keyValue;

    @Column (name = "procured_date")
    private Date procurDate;

    @Column (name = "expiry_date")
    private Date expiryDate;
    
    @Column (name = "status")
    @Enumerated
    private Status status;

    @Column (name = "user_id")
    private long userId;
}

