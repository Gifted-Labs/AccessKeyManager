package com.juls.accesskeymanager.exceptions;

public class InvalidPasswordException extends RuntimeException{
    
    public InvalidPasswordException (String message){
        super(message);
    }
}
