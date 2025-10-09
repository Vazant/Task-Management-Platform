package com.taskboard.userservice.domain.exception;

/**
 * Base exception for all domain-related errors in the User Service.
 * 
 * <p>This exception serves as the root of the domain exception hierarchy
 * and should be used for all business rule violations and domain-specific
 * error conditions. It provides a common base for all domain exceptions
 * and ensures consistent error handling across the domain layer.</p>
 * 
 * <p>Domain exceptions should be thrown when:</p>
 * <ul>
 *   <li>Business rules are violated</li>
 *   <li>Domain invariants are broken</li>
 *   <li>Invalid domain operations are attempted</li>
 *   <li>Domain state transitions are not allowed</li>
 * </ul>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserDomainException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new UserDomainException with the specified detail message.
     * 
     * @param message the detail message
     */
    public UserDomainException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new UserDomainException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public UserDomainException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new UserDomainException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public UserDomainException(Throwable cause) {
        super(cause);
    }
}
