package com.taskboard.user.dto;

import lombok.*;

/**
 * DTO for refresh token response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenResponse {
    
    private String token;
    private String refreshToken;
}