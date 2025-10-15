package com.taskboard.userservice.application.dto;

import com.taskboard.userservice.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Request DTO for creating a new user.
 *
 * <p>This DTO contains all the necessary information required to create a new user in the system.
 * It includes validation annotations to ensure data integrity and proper formatting.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * CreateUserRequest request = CreateUserRequest.builder()
 *     .username("johndoe")
 *     .email("john@example.com")
 *     .password("securePassword123")
 *     .firstName("John")
 *     .lastName("Doe")
 *     .role(UserRole.USER)
 *     .profileImageUrl("https://example.com/avatar.jpg")
 *     .build();
 * }</pre>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see UserRole
 * @see CreateUserResponse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class CreateUserRequest {

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores, and hyphens")
  private String username;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
  private String password;

  @NotBlank(message = "First name is required")
  @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
  private String lastName;

  @NotNull(message = "Role is required")
  private UserRole role;

  @Pattern(
	regexp = "^(?:https?://)?(?:[A-Za-z0-9-]++\\\\.)++[A-Za-z]{2,63}(?:/[\\\\w.-]++)*+/?$",
	message = "Profile image URL must be a valid URL"
  )
  private String profileImageUrl;
  
  
}