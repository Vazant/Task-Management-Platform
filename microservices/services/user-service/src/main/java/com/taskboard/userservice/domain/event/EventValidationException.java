package com.taskboard.userservice.domain.event;

/**
 * Exception thrown when event validation fails.
 * 
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
public class EventValidationException extends RuntimeException {
    
    public EventValidationException(String message) {
        super(message);
    }
    
    public EventValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
