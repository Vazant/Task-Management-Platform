package com.taskboard.userservice.domain.exception;

/**
 * Exception thrown when attempting to create a user that already exists.
 * 
 * <p>This exception is thrown when trying to create a new user with
 * credentials (username or email) that are already in use by another
 * user in the system. It helps maintain data integrity and prevents
 * duplicate user accounts.</p>
 * 
 * <p>This exception should be thrown when:</p>
 * <ul>
 *   <li>Attempting to create a user with an existing username</li>
 *   <li>Attempting to create a user with an existing email</li>
 *   <li>Any operation that would result in duplicate user credentials</li>
 * </ul>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserAlreadyExistsException extends UserDomainException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new UserAlreadyExistsException with the specified username.
     * 
     * @param username the username that already exists
     */
    public UserAlreadyExistsException(String username) {
        super("User already exists with username: " + username);
    }
    
    /**
     * Constructs a new UserAlreadyExistsException with the specified email.
     * 
     * @param email the email that already exists
     */
    public UserAlreadyExistsException(String email, boolean isEmail) {
        super("User already exists with email: " + email);
    }
    
    /**
     * Constructs a new UserAlreadyExistsException with the specified detail message.
     * 
     * @param message the detail message
     */
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
