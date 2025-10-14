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
 * Response DTO for user authentication operation.
 *
 * <p>This DTO contains the authentication result including access token,
 * refresh token, and user information.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * AuthenticateUserResponse response = AuthenticateUserResponse.builder()
 *     .accessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
 *     .refreshToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
 *     .user(AuthenticatedUserDto.fromDomain(user))
 *     .build();
 * }</pre>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see AuthenticateUserRequest
 * @see AuthenticatedUserDto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateUserResponse {

  private String accessToken;
  private String refreshToken;
  private AuthenticatedUserDto user;

  /**
   * DTO for authenticated user information.
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AuthenticatedUserDto {
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
     * Creates an AuthenticatedUserDto from a User domain model.
     *
     * @param user the User domain model
     * @return a new AuthenticatedUserDto instance
     */
    public static AuthenticatedUserDto fromDomain(User user) {
      return AuthenticatedUserDto.builder()
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
  }
}




