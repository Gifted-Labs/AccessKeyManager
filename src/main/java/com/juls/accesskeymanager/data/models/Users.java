package com.juls.accesskeymanager.data.models;

import org.hibernate.annotations.NaturalId;

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
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NaturalId(mutable = true)
    @Column (name="email")
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated
    @Column(name ="role")
    private Role role; 

    private boolean isEnabled = false;
}



