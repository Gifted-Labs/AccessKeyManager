package com.juls.accesskeymanager.services;

import java.util.List;

import org.springframework.stereotype.Component;

import com.juls.accesskeymanager.data.models.AuthenticationRequest;
import com.juls.accesskeymanager.data.models.Users;


@Component
public interface UserService{
    String validateToken(String token);
    
    Users getUserByEmail(String email);

    List <Users> getUsers();

    Users registerUser(AuthenticationRequest request);

}
