package com.taskboard.userservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for user deletion operation.
 *
 * <p>This DTO contains the result of a user deletion operation.
 * It provides confirmation that the user was successfully deleted.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * DeleteUserResponse response = DeleteUserResponse.builder()
 *     .success(true)
 *     .message("User deleted successfully")
 *     .deletedUserId(123L)
 *     .build();
 * }</pre>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see DeleteUserRequest
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserResponse {

  private boolean success;
  private String message;
  private Long deletedUserId;

  /**
   * Creates a successful deletion response.
   *
   * @param deletedUserId the ID of the deleted user
   * @return a new DeleteUserResponse instance
   */
  public static DeleteUserResponse success(Long deletedUserId) {
    return DeleteUserResponse.builder()
        .success(true)
        .message("User deleted successfully")
        .deletedUserId(deletedUserId)
        .build();
  }

  /**
   * Creates a failed deletion response.
   *
   * @param message the error message
   * @return a new DeleteUserResponse instance
   */
  public static DeleteUserResponse failure(String message) {
    return DeleteUserResponse.builder()
        .success(false)
        .message(message)
        .deletedUserId(null)
        .build();
  }
}


