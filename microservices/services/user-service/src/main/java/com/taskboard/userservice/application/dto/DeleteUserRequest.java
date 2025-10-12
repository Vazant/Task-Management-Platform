package com.taskboard.userservice.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for deleting a user by ID.
 *
 * <p>This DTO contains the necessary information to delete a specific user from the system.
 * It includes validation to ensure the user ID is valid.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * DeleteUserRequest request = DeleteUserRequest.builder()
 *     .userId(123L)
 *     .build();
 * }</pre>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see DeleteUserResponse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserRequest {

  @NotNull(message = "User ID cannot be null")
  @Positive(message = "User ID must be positive")
  private Long userId;
}



