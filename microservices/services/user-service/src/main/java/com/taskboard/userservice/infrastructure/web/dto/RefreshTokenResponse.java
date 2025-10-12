package com.taskboard.userservice.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for successful token refresh.
 * Contains the new authentication token and related information.
 * 
 * <p>This DTO is returned after successful token refresh and includes:
 * <ul>
 *   <li>New JWT access token</li>
 *   <li>Token type and expiration information</li>
 *   <li>New refresh token for future renewals</li>
 * </ul>
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 * @see RefreshTokenRequest
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponse {
    
    /**
     * New JWT authentication token for API access.
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
     * New refresh token for future token renewals.
     */
    private String refreshToken;
}
