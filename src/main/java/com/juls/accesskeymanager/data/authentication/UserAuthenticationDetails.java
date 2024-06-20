package com.juls.accesskeymanager.data.authentication;

import java.util.Collection;
import java.util.Collections;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.juls.accesskeymanager.data.models.Role;
import com.juls.accesskeymanager.data.models.Users;

/**
 * Implementation of Spring Security's UserDetails interface for representing authenticated user details.
 * This class encapsulates user-specific information retrieved from the database.
 */
public class UserAuthenticationDetails implements UserDetails {

    private String username;

    private String password;
    private boolean isEnabled;
    private  Collection <? extends GrantedAuthority> authorities;

    /**
     * Constructs a new UserAuthenticationDetails object based on the provided Users entity.
     * @param user The Users entity containing user-specific information.
     */
    public UserAuthenticationDetails (Users user){
        this.username =  user.getEmail();
        this.password = user.getPassword();
        this.isEnabled = user.isEnabled();

        this.authorities = mapRolesToAuthorities(user.getRole());

    }



    /**
     * Maps the user's role to Spring Security's GrantedAuthority format.
     * @param role The role of the user.
     * @return A collection of GrantedAuthority representing the user's role.
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Role role){
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }


    /**
     * Retrieves the authorities granted to the authenticated user.
     * @return A collection of GrantedAuthority representing the user's authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /**
     * Retrieves the password used to authenticate the user.
     * @return The user's password.
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Retrieves the username used to authenticate the user.
     * @return The user's username.
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * Indicates whether the user's account has not expired.
     * @return Always returns true, indicating the account never expires.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is not locked.
     * @return Always returns true, indicating the user is never locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) are not expired.
     * @return Always returns true, indicating the credentials never expire.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user account is currently enabled.
     * @return true if the user account is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    


    
}
