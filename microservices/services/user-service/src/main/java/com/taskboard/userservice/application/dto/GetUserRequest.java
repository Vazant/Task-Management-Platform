package com.taskboard.userservice.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for retrieving a user by ID.
 *
 * <p>This DTO contains the necessary information to retrieve a specific user from the system.
 * It includes validation to ensure the user ID is valid.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * GetUserRequest request = GetUserRequest.builder()
 *     .userId(123L)
 *     .build();
 * }</pre>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see GetUserResponse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserRequest {

  @NotNull(message = "User ID is required")
  @Positive(message = "User ID must be positive")
  private Long userId;
}



