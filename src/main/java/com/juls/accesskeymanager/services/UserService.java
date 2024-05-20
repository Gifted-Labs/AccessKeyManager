package com.juls.accesskeymanager.services;

import java.util.List;

import org.springframework.stereotype.Component;

import com.juls.accesskeymanager.data.models.AuthenticationRequest;
import com.juls.accesskeymanager.data.models.Users;



public interface UserService{
    
    Users getUserByEmail(String email);
    Users registerUser (AuthenticationRequest authenticationRequest);
    List <Users> getUsers();
    String validateToken (String token);
    
}
