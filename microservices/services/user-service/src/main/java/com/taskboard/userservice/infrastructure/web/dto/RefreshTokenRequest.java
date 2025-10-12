package com.taskboard.userservice.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for authentication token refresh.
 * Contains the refresh token needed to obtain a new access token.
 * 
 * <p>This DTO is used for the token refresh endpoint to extend
 * user sessions without requiring re-authentication.
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 * @see RefreshTokenResponse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
    
    /**
     * The refresh token used to obtain a new access token.
     */
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
