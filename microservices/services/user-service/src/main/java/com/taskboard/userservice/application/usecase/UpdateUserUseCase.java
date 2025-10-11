package com.taskboard.userservice.application.usecase;

import com.taskboard.userservice.application.dto.UpdateUserRequest;
import com.taskboard.userservice.application.dto.UpdateUserResponse;
import com.taskboard.userservice.domain.exception.UserNotFoundException;
import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.repository.UserRepository;
import com.taskboard.userservice.domain.service.UserDomainService;
import com.taskboard.userservice.infrastructure.monitoring.PerformanceMonitoringService;
import com.taskboard.userservice.infrastructure.monitoring.SecurityAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Use case for updating an existing user.
 *
 * <p>This use case handles the business logic for updating a user in the system.
 * It includes validation, error handling, monitoring, and auditing.
 *
 * <p>The use case follows the following flow:
 * <ol>
 *   <li>Validate the request data</li>
 *   <li>Check if the user exists</li>
 *   <li>Validate business rules through domain service</li>
 *   <li>Update the user data</li>
 *   <li>Save the updated user</li>
 *   <li>Log the operation and record metrics</li>
 *   <li>Return the updated user data</li>
 * </ol>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see UpdateUserRequest
 * @see UpdateUserResponse
 * @see User
 * @see UserRepository
 * @see UserDomainService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateUserUseCase {

  private final UserRepository userRepository;
  private final UserDomainService userDomainService;
  private final SecurityAuditService securityAuditService;
  private final PerformanceMonitoringService performanceMonitoringService;

  /**
   * Executes the user update operation.
   *
   * @param request the update user request containing the user ID and updated data
   * @return the updated user response
   * @throws IllegalArgumentException if the request is invalid
   * @throws UserNotFoundException if the user with the specified ID does not exist
   * @throws RuntimeException if an unexpected error occurs during the operation
   */
  public UpdateUserResponse execute(UpdateUserRequest request) {
    long startTime = System.currentTimeMillis();
    
    try {
      if (request == null) {
        throw new IllegalArgumentException("Update user request cannot be null");
      }
      
      log.info("Starting user update operation for user ID: {}", request.getUserId());
      
      // Validate request
      validateRequest(request);
      
      // Retrieve existing user
      User existingUser = userRepository.findById(request.getUserId())
          .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId(), null));
      
      // Validate business rules
      userDomainService.validateUserUpdate(existingUser, request);
      
      // Update user data
      updateUserData(existingUser, request);
      
      // Save updated user
      User updatedUser = userRepository.save(existingUser);
      
      // Log successful operation
      securityAuditService.logUserUpdated(updatedUser.getId(), updatedUser.getUsername());
      performanceMonitoringService.recordUserUpdateTime(System.currentTimeMillis() - startTime);
      
      log.info("User update operation completed successfully for user ID: {}", updatedUser.getId());
      
      return UpdateUserResponse.fromDomain(updatedUser);
      
    } catch (UserNotFoundException e) {
      handleUserUpdateFailure(request != null ? request.getUserId() : null, e.getMessage(), startTime);
      throw e;
    } catch (IllegalArgumentException e) {
      handleUserUpdateFailure(request != null ? request.getUserId() : null, e.getMessage(), startTime);
      throw e;
    } catch (Exception e) {
      handleUserUpdateFailure(request != null ? request.getUserId() : null, e.getMessage(), startTime);
      log.error("Unexpected error during user update for user ID: {}", request != null ? request.getUserId() : null, e);
      throw new RuntimeException("Failed to update user", e);
    }
  }

  /**
   * Validates the update user request.
   *
   * @param request the request to validate
   * @throws IllegalArgumentException if the request is invalid
   */
  private void validateRequest(UpdateUserRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Update user request cannot be null");
    }
    
    if (request.getUserId() == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
    
    if (request.getUserId() <= 0) {
      throw new IllegalArgumentException("User ID must be positive");
    }
    
    // Check if at least one field is provided for update
    if (request.getEmail() == null && 
        request.getFirstName() == null && 
        request.getLastName() == null && 
        request.getRole() == null && 
        request.getProfileImageUrl() == null) {
      throw new IllegalArgumentException("At least one field must be provided for update");
    }
  }

  /**
   * Updates the user data with the provided request data.
   *
   * @param user the existing user to update
   * @param request the update request containing new data
   */
  private void updateUserData(User user, UpdateUserRequest request) {
    if (request.getEmail() != null) {
      user.updateEmail(request.getEmail());
    }
    
    if (request.getFirstName() != null) {
      user.updateFirstName(request.getFirstName());
    }
    
    if (request.getLastName() != null) {
      user.updateLastName(request.getLastName());
    }
    
    if (request.getRole() != null) {
      user.updateRole(request.getRole());
    }
    
    if (request.getProfileImageUrl() != null) {
      user.updateProfileImageUrl(request.getProfileImageUrl());
    }
  }

  /**
   * Handles user update failure by logging and recording metrics.
   *
   * @param userId the user ID
   * @param reason the reason for failure
   * @param startTime the operation start time
   */
  private void handleUserUpdateFailure(Long userId, String reason, long startTime) {
    securityAuditService.logUserUpdateFailed(userId, reason);
    performanceMonitoringService.recordUserUpdateFailure(System.currentTimeMillis() - startTime);
  }
}
