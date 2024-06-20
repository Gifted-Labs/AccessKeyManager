    package com.juls.accesskeymanager.exceptions;

    import lombok.Data;
    import org.springframework.http.HttpStatus;
    import org.springframework.web.bind.annotation.ResponseStatus;

    /**
     * Exception thrown to indicate that a resource or entity was not found.
     *
     * <p>This exception is typically used to indicate that a requested resource or entity does not exist,
     * which corresponds to the HTTP status code 404 (Not Found).
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class NotFoundException extends Exception{

        /**
         * Constructs a new {@code NotFoundException} with {@code null} as its detail message.
         */
        public NotFoundException() {
            super();
        }

        /**
         * Constructs a new {@code NotFoundException} with the specified detail message.
         *
         * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
         */
        public NotFoundException(String message) {
            super(message);
        }


        /**
         * Constructs a new {@code NotFoundException} with the specified detail message and cause.
         *
         * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
         * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
         */
        public NotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Constructs a new {@code NotFoundException} with the specified cause and a detail message of
         * {@code (cause==null ? null : cause.toString())} (which typically contains the class and detail message
         * of {@code cause}). This constructor is useful for exceptions that are little more than wrappers
         * for other throwables (for example, {@link java.security.PrivilegedActionException}).
         *
         * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
         */
        public NotFoundException(Throwable cause) {
            super(cause);
        }

        /**
         * Constructs a new {@code NotFoundException} with the specified detail message, cause, suppression
         * enabled or disabled, and writable stack trace enabled or disabled.
         *
         * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
         * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
         * @param enableSuppression whether or not suppression is enabled or disabled
         * @param writableStackTrace whether or not the stack trace should be writable
         */
        protected NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
