package com.taskboard.userservice.application.usecase;

import com.taskboard.userservice.application.dto.GetUserRequest;
import com.taskboard.userservice.application.dto.GetUserResponse;
import com.taskboard.userservice.domain.exception.UserNotFoundException;
import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.repository.UserRepository;
import com.taskboard.userservice.infrastructure.monitoring.PerformanceMonitoringService;
import com.taskboard.userservice.infrastructure.monitoring.SecurityAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Use case for retrieving a user by ID.
 *
 * <p>This use case handles the business logic for retrieving a user from the system.
 * It includes validation, error handling, and monitoring.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * GetUserRequest request = GetUserRequest.builder()
 *     .userId(123L)
 *     .build();
 * 
 * GetUserResponse response = getUserUseCase.execute(request);
 * }</pre>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see GetUserRequest
 * @see GetUserResponse
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserUseCase {

  private final UserRepository userRepository;
  private final SecurityAuditService securityAuditService;
  private final PerformanceMonitoringService performanceMonitoringService;

  /**
   * Executes the get user use case.
   *
   * @param request the get user request
   * @return the user response
   * @throws IllegalArgumentException if request is null or invalid
   * @throws UserNotFoundException if user is not found
   */
  public GetUserResponse execute(GetUserRequest request) {
    long startTime = System.currentTimeMillis();
    
    try {
      // Validate request
      validateRequest(request);
      
      // Retrieve user from repository
      User user = userRepository.findById(request.getUserId())
          .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId(), null));
      
      // Record successful operation
      long executionTime = System.currentTimeMillis() - startTime;
      securityAuditService.logUserRetrieved(request.getUserId(), user.getUsername());
      performanceMonitoringService.recordUserRetrievalTime(executionTime);
      
      log.debug("User retrieved successfully: {}", user.getUsername());
      
      // Convert to response DTO
      return GetUserResponse.fromUser(user);
      
    } catch (UserNotFoundException e) {
      // Record failed attempt in security audit
      securityAuditService.logUserRetrievalFailed(request.getUserId(), e.getMessage());
      
      // Record performance metrics for failed operation
      long executionTime = System.currentTimeMillis() - startTime;
      performanceMonitoringService.recordUserRetrievalFailure(executionTime);
      
      throw e;
      
    } catch (Exception e) {
      // Record unexpected error in security audit
      securityAuditService.logUserRetrievalFailed(request.getUserId(), "Unexpected error: " + e.getMessage());
      
      // Record performance metrics for failed operation
      long executionTime = System.currentTimeMillis() - startTime;
      performanceMonitoringService.recordUserRetrievalFailure(executionTime);
      
      log.error("Unexpected error during user retrieval for user ID: {}", request.getUserId(), e);
      throw new RuntimeException("Failed to retrieve user: " + e.getMessage(), e);
    }
  }

  /**
   * Validates the get user request.
   *
   * @param request the request to validate
   * @throws IllegalArgumentException if request is null or invalid
   */
  private void validateRequest(GetUserRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Get user request cannot be null");
    }
    
    if (request.getUserId() == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
    
    if (request.getUserId() <= 0) {
      throw new IllegalArgumentException("User ID must be positive");
    }
  }
}
