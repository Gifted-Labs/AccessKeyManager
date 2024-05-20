package com.juls.accesskeymanager.data.models;

import lombok.Data;

@Data
public class AuthenticationRequest {

    private String email;
    private String password;
    private Role role;

    
}
