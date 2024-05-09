package com.juls.accesskeymanager.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.data.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService{
    
    @Autowired
    private UserRepository userRepository;


    @Override
    public String getUserEmailById(Long id){
        Optional <Users> user = this.userRepository.findById(id);
        return user.get().getEmail();
    }

    @Override
    public Long getUserIdByEmail(String email){
        Optional <Users> user = this.userRepository.findByEmail(email);
        return user.get().getUserId();
    }
    
}
