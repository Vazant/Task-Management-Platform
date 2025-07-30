package com.taskboard.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO for refresh token request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
