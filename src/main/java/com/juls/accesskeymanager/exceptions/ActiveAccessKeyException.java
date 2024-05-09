package com.juls.accesskeymanager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ActiveAccessKeyException extends Exception {

    public ActiveAccessKeyException() {
        super();
    }

    public ActiveAccessKeyException(String message) {
        super(message);
    }

    public ActiveAccessKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActiveAccessKeyException(Throwable cause) {
        super(cause);
    }

    protected ActiveAccessKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
