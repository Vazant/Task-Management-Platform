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
 * Response DTO for user update operation.
 *
 * <p>This DTO contains the information about an updated user that should be returned to the
 * client after a successful user update operation.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * UpdateUserResponse response = UpdateUserResponse.builder()
 *     .id(123L)
 *     .username("johndoe")
 *     .email("newemail@example.com")
 *     .firstName("Updated First Name")
 *     .lastName("Updated Last Name")
 *     .status(UserStatus.ACTIVE)
 *     .role(UserRole.ADMIN)
 *     .createdAt(LocalDateTime.now())
 *     .updatedAt(LocalDateTime.now())
 *     .emailVerified(true)
 *     .profileImageUrl("https://example.com/new-avatar.jpg")
 *     .build();
 * }</pre>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see User
 * @see UserRole
 * @see UserStatus
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserResponse {

  private Long id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private UserStatus status;
  private UserRole role;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean emailVerified;
  private String profileImageUrl;

  /**
   * Creates an UpdateUserResponse from a User domain model.
   *
   * @param user the User domain model
   * @return a new UpdateUserResponse instance
   */
  public static UpdateUserResponse fromDomain(User user) {
    return UpdateUserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .status(user.getStatus())
        .role(user.getRole())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .emailVerified(user.isEmailVerified())
        .profileImageUrl(user.getProfileImageUrl())
        .build();
  }
}



