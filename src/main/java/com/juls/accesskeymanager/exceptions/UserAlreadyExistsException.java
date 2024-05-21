package com.juls.accesskeymanager.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String message){
        super(message);
    }

}
