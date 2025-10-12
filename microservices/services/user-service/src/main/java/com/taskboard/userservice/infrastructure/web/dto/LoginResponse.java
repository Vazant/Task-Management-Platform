package com.taskboard.userservice.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for successful user authentication (login).
 * Contains the authentication token and user information.
 * 
 * <p>This DTO is returned after successful login and includes:
 * <ul>
 *   <li>JWT access token for API authentication</li>
 *   <li>Token type and expiration information</li>
 *   <li>User identification details</li>
 *   <li>Optional refresh token for token renewal</li>
 * </ul>
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 * @see LoginRequest
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    /**
     * JWT authentication token for API access.
     */
    private String token;
    
    /**
     * Token type (typically "Bearer").
     */
    private String tokenType;
    
    /**
     * Token expiration time in seconds.
     */
    private Long expiresIn;
    
    /**
     * Unique identifier of the authenticated user.
     */
    private Long userId;
    
    /**
     * Username of the authenticated user.
     */
    private String username;
    
    /**
     * Email address of the authenticated user.
     */
    private String email;
    
    /**
     * Role of the authenticated user.
     */
    private String role;
    
    /**
     * Refresh token for token renewal (if used).
     */
    private String refreshToken;
}
