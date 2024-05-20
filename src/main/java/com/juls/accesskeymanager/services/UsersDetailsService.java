package com.juls.accesskeymanager.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.juls.accesskeymanager.data.authentication.UserAuthenticationDetails;
import com.juls.accesskeymanager.data.repository.UserRepository;

@Service
public class UsersDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email){
        return userRepository.findByEmail(email).map(UserAuthenticationDetails::new).orElseThrow(() -> new UsernameNotFoundException("User with email "+ email +" not found"));
    }
    
    
}
