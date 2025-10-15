package com.taskboard.userservice.application.dto;

import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Request DTO for updating an existing user.
 *
 * <p>This DTO contains the necessary information to update a user in the system.
 * It includes validation annotations to ensure data integrity and proper formatting.
 * All fields except userId are optional to allow partial updates.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * UpdateUserRequest request = UpdateUserRequest.builder()
 *     .userId(123L)
 *     .email("newemail@example.com")
 *     .firstName("Updated First Name")
 *     .lastName("Updated Last Name")
 *     .role(UserRole.ADMIN)
 *     .profileImageUrl("https://example.com/new-avatar.jpg")
 *     .build();
 * }</pre>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see UserRole
 * @see UpdateUserResponse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"email"}) // Exclude email from toString for security
public class UpdateUserRequest {

  @NotNull(message = "User ID is required")
  @Positive(message = "User ID must be positive")
  private Long userId;

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  private String username;

  @Email(message = "Email must be valid")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
  private String firstName;

  @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
  private String lastName;

  private UserRole role;

  private UserStatus status;

  @Pattern(
    regexp = "^(?:https?://)?(?:[A-Za-z0-9-]++\\.)++[A-Za-z]{2,63}(?:/[\\w.-]++)*+/?$",
    message = "Profile image URL must be a valid URL"
  )
  private String profileImageUrl;

}





