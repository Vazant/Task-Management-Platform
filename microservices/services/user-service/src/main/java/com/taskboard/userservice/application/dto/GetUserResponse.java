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
 * Response DTO for user retrieval operation.
 *
 * <p>This DTO contains the information about a retrieved user that should be returned to the
 * client. It includes all the essential user information without sensitive data like password.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * GetUserResponse response = GetUserResponse.builder()
 *     .id(user.getId())
 *     .username(user.getUsername())
 *     .email(user.getEmail())
 *     .firstName(user.getFirstName())
 *     .lastName(user.getLastName())
 *     .status(user.getStatus())
 *     .role(user.getRole())
 *     .createdAt(user.getCreatedAt())
 *     .updatedAt(user.getUpdatedAt())
 *     .lastLoginAt(user.getLastLoginAt())
 *     .emailVerified(user.isEmailVerified())
 *     .profileImageUrl(user.getProfileImageUrl())
 *     .build();
 * }</pre>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see User
 * @see GetUserRequest
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserResponse {

  private Long id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private UserStatus status;
  private UserRole role;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime lastLoginAt;
  private boolean emailVerified;
  private String profileImageUrl;

  /**
   * Creates a GetUserResponse from a User entity.
   *
   * @param user the user entity
   * @return a GetUserResponse instance
   */
  public static GetUserResponse fromUser(User user) {
    return GetUserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .status(user.getStatus())
        .role(user.getRole())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .lastLoginAt(user.getLastLoginAt())
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
        .updatedAt(this.updatedAt)
        .lastLoginAt(this.lastLoginAt)
        .emailVerified(this.emailVerified)
        .profileImageUrl(this.profileImageUrl)
        .build();
  }
}





