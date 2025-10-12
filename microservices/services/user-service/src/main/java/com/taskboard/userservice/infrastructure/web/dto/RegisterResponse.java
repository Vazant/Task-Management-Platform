package com.taskboard.userservice.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for successful user registration.
 * Contains information about the newly created user account.
 * 
 * <p>This DTO is returned after successful registration and includes:
 * <ul>
 *   <li>User identification details</li>
 *   <li>Registration confirmation message</li>
 *   <li>Email verification status</li>
 * </ul>
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 * @see RegisterRequest
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    
    /**
     * Unique identifier of the created user.
     */
    private Long userId;
    
    /**
     * Username of the created user.
     */
    private String username;
    
    /**
     * Email address of the created user.
     */
    private String email;
    
    /**
     * First name of the created user.
     */
    private String firstName;
    
    /**
     * Last name of the created user.
     */
    private String lastName;
    
    /**
     * Registration confirmation message.
     */
    private String message;
    
    /**
     * Flag indicating whether email verification is required.
     */
    private boolean emailVerificationRequired;
}
