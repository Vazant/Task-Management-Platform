package com.taskboard.userservice.application.dto;

import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Response DTO for user creation operation.
 *
 * <p>This DTO contains the information about a newly created user that should be returned to the
 * client. It includes all the essential user information without sensitive data like password.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * CreateUserResponse response = CreateUserResponse.builder()
 *     .id(user.getId())
 *     .username(user.getUsername())
 *     .email(user.getEmail())
 *     .firstName(user.getFirstName())
 *     .lastName(user.getLastName())
 *     .status(user.getStatus())
 *     .role(user.getRole())
 *     .createdAt(user.getCreatedAt())
 *     .emailVerified(user.isEmailVerified())
 *     .profileImageUrl(user.getProfileImageUrl())
 *     .build();
 * }</pre>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see User
 * @see CreateUserRequest
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserResponse {

  private Long id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private UserStatus status;
  private UserRole role;
  private LocalDateTime createdAt;
  private boolean emailVerified;
  private String profileImageUrl;

  /**
   * Creates a CreateUserResponse from a User entity.
   *
   * @param user the user entity
   * @return a CreateUserResponse instance
   */
  public static CreateUserResponse fromUser(User user) {
    return CreateUserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .status(user.getStatus())
        .role(user.getRole())
        .createdAt(user.getCreatedAt())
        .emailVerified(user.isEmailVerified())
        .profileImageUrl(user.getProfileImageUrl())
        .build();
  }

  /**
   * Converts this response to a UserDto.
   *
   * @return a UserDto instance
   */
  public UserDto getUser() {
    return UserDto.builder()
        .id(this.id)
        .username(this.username)
        .email(this.email)
        .firstName(this.firstName)
        .lastName(this.lastName)
        .status(this.status)
        .role(this.role)
        .createdAt(this.createdAt)
        .emailVerified(this.emailVerified)
        .profileImageUrl(this.profileImageUrl)
        .build();
  }

  /**
   * Gets the user ID.
   *
   * @return the user ID
   */
  public Long getUserId() {
    return this.id;
  }

  /**
   * Checks if the user is active.
   *
   * @return true if the user is active
   */
  public boolean isActive() {
    return this.status == UserStatus.ACTIVE;
  }
}