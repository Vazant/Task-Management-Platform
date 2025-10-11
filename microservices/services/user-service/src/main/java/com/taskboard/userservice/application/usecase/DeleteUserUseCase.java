package com.taskboard.userservice.application.usecase;

import com.taskboard.userservice.application.dto.DeleteUserRequest;
import com.taskboard.userservice.application.dto.DeleteUserResponse;
import com.taskboard.userservice.domain.exception.UserNotFoundException;
import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.repository.UserRepository;
import com.taskboard.userservice.infrastructure.monitoring.PerformanceMonitoringService;
import com.taskboard.userservice.infrastructure.monitoring.SecurityAuditService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/**
 * Use case for deleting a user from the system.
 *
 * <p>This use case handles the business logic for deleting a user.
 * It includes validation, error handling, and integration with monitoring services.
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see DeleteUserRequest
 * @see DeleteUserResponse
 * @see UserRepository
 * @see SecurityAuditService
 * @see PerformanceMonitoringService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteUserUseCase {

  private final UserRepository userRepository;
  private final SecurityAuditService securityAuditService;
  private final PerformanceMonitoringService performanceMonitoringService;
  private final Validator validator; // Injecting Jakarta Validator

  /**
   * Executes the user deletion operation.
   *
   * @param request the {@link DeleteUserRequest} containing the user ID
   * @return a {@link DeleteUserResponse} with the deletion result
   * @throws IllegalArgumentException if the request is null or invalid
   * @throws UserNotFoundException if no user is found with the given ID
   */
  @Transactional
  public DeleteUserResponse execute(@Valid DeleteUserRequest request) {
    Instant start = Instant.now();
    Long userId = null;

    try {
      if (request == null) {
        throw new IllegalArgumentException("Delete user request cannot be null");
      }

      // Manual validation using Jakarta Validator
      Set<ConstraintViolation<DeleteUserRequest>> violations = validator.validate(request);
      if (!violations.isEmpty()) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<DeleteUserRequest> violation : violations) {
          sb.append(violation.getMessage()).append("; ");
        }
        throw new IllegalArgumentException("Validation failed: " + sb.toString());
      }

      userId = request.getUserId();
      log.info("Starting user deletion operation for user ID: {}", userId);

      // Check if user exists
      Optional<User> userOptional = userRepository.findById(userId);
      if (userOptional.isEmpty()) {
        throw new UserNotFoundException("User not found with id: " + userId, null);
      }

      User userToDelete = userOptional.get();
      
      // Log the deletion attempt
      securityAuditService.logUserDeletionAttempt(userId, userToDelete.getUsername());
      
      // Delete the user
      userRepository.delete(userToDelete);
      
      // Log successful deletion
      securityAuditService.logUserDeleted(userId, userToDelete.getUsername());
      log.info("User deletion operation completed successfully for user ID: {}", userId);

      return DeleteUserResponse.success(userId);
      
    } catch (UserNotFoundException e) {
      handleUserDeletionFailure(userId, "User not found", e);
      throw e;
    } catch (IllegalArgumentException e) {
      handleUserDeletionFailure(userId, "Invalid request: " + e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      handleUserDeletionFailure(userId, "Unexpected error: " + e.getMessage(), e);
      throw new RuntimeException("Failed to delete user with ID: " + userId, e);
    } finally {
      long duration = Duration.between(start, Instant.now()).toMillis();
      performanceMonitoringService.recordUserDeletionTime(duration);
    }
  }

  /**
   * Handles user deletion failure by logging and recording metrics.
   *
   * @param userId the user ID
   * @param reason the reason for failure
   * @param e the exception that occurred
   */
  private void handleUserDeletionFailure(Long userId, String reason, Exception e) {
    securityAuditService.logUserDeletionFailed(userId, reason);
    performanceMonitoringService.recordUserDeletionFailure(Duration.between(Instant.now(), Instant.now()).toMillis()); // Duration is 0 for immediate failure
    log.error("User deletion failed for ID {}: {}", userId, reason, e);
  }
}
