package com.juls.accesskeymanager.data.authentication;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.juls.accesskeymanager.data.models.Role;
import com.juls.accesskeymanager.data.models.Users;

public class UserAuthenticationDetails implements UserDetails {

    private String username;
    private String password;
    private boolean isEnabled;
    private  Collection <? extends GrantedAuthority> authorities;

    public UserAuthenticationDetails (Users user){
        this.username =  user.getEmail();
        this.password = user.getPassword();
        this.isEnabled = user.isEnabled();

        this.authorities = mapRolesToAuthorities(user.getRole());

    }


    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Role role){
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    


    
}
