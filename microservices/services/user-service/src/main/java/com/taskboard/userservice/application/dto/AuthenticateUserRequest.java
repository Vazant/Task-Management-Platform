package com.taskboard.userservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user authentication.
 *
 * <p>This DTO contains the credentials provided by the user for authentication.
 * It includes validation to ensure the credentials are properly formatted.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * AuthenticateUserRequest request = AuthenticateUserRequest.builder()
 *     .username("johndoe")
 *     .password("securePassword123")
 *     .build();
 * }</pre>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see AuthenticateUserResponse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateUserRequest {

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  private String username;

  @NotBlank(message = "Password is required")
  @Size(min = 1, max = 128, message = "Password must not exceed 128 characters")
  private String password;
}

