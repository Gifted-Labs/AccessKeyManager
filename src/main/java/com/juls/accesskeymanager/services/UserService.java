package com.juls.accesskeymanager.services;

import org.springframework.stereotype.Component;


@Component
public interface UserService{
    
    public String getUserEmailById(Long id);

    public Long getUserIdByEmail(String email);
}
