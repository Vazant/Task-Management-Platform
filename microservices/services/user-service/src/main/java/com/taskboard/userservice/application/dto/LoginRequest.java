package com.taskboard.userservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user authentication (login).
 * Contains the credentials required to authenticate a user.
 * 
 * <p>This DTO is used for the login endpoint and includes:
 * <ul>
 *   <li>Username or email for identification</li>
 *   <li>Password for authentication</li>
 *   <li>Remember me flag for extended sessions</li>
 * </ul>
 * 
 * <p>All fields are validated according to security requirements.
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see LoginResponse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    /**
     * Username or email address for user identification.
     */
    @NotBlank(message = "Username or email is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;
    
    /**
     * User password for authentication.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
    
    /**
     * Flag indicating whether to extend the session duration.
     */
    private boolean rememberMe;
}


