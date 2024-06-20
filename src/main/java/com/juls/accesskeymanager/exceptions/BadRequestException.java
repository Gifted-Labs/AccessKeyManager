package com.juls.accesskeymanager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Exception thrown to indicate that a client request is invalid due to malformed syntax,
 * missing parameters, or other request-related errors.
 *
 * <p>This exception is typically used to indicate a problem with the client's request that
 * prevented the server from processing it as expected. It corresponds to the HTTP status code 400
 * (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends Exception{


    /**
     * Constructs a new {@code BadRequestException} with {@code null} as its detail message.
     */
    public BadRequestException() {
        super();
    }

    /**
     * Constructs a new {@code BadRequestException} with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     */
    public BadRequestException(String message) {
        super(message);
    }


    /**
     * Constructs a new {@code BadRequestException} with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code BadRequestException} with the specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())} (which typically contains the class and detail message
     * of {@code cause}). This constructor is useful for exceptions that are little more than wrappers
     * for other throwables (for example, {@link java.security.PrivilegedActionException}).
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public BadRequestException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code BadRequestException} with the specified detail message, cause, suppression
     * enabled or disabled, and writable stack trace enabled or disabled.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     */
    protected BadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
