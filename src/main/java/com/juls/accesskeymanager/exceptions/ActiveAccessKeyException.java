package com.juls.accesskeymanager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to perform an operation on an active access key that conflicts
 * with business rules or security policies.
 *
 * <p>This exception indicates that the operation cannot proceed because it would violate constraints
 * related to active access keys.
 *
 * <p>Typically, this exception is thrown in scenarios where an operation is attempted on an access key
 * that is currently active and cannot be modified or deleted according to application rules.
 *
 * <p>Instances of this exception will result in an HTTP status code of 403 (Forbidden) when handled
 * by Spring's exception handling mechanism.
 */

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ActiveAccessKeyException extends RuntimeException {

    /**
     * Constructs a new {@code ActiveAccessKeyException} with {@code null} as its detail message.
     */
    public ActiveAccessKeyException() {
        super();
    }

    /**
     * Constructs a new {@code ActiveAccessKeyException} with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     */
    public ActiveAccessKeyException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ActiveAccessKeyException} with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public ActiveAccessKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code ActiveAccessKeyException} with the specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())} (which typically contains the class and detail message
     * of {@code cause}). This constructor is useful for exceptions that are little more than wrappers
     * for other throwables (for example, {@link java.security.PrivilegedActionException}).
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public ActiveAccessKeyException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code ActiveAccessKeyException} with the specified detail message, cause, suppression
     * enabled or disabled, and writable stack trace enabled or disabled.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     */
    protected ActiveAccessKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
