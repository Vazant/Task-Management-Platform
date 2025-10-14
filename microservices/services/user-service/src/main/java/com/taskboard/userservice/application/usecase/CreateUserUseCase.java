package com.taskboard.userservice.application.usecase;

import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.dto.CreateUserResponse;
import com.taskboard.userservice.domain.exception.UserAlreadyExistsException;
import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.repository.UserRepository;
import com.taskboard.userservice.domain.service.UserDomainService;
import com.taskboard.userservice.infrastructure.monitoring.PerformanceMonitoringService;
import com.taskboard.userservice.infrastructure.monitoring.SecurityAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for creating a new user.
 *
 * <p>This use case handles the business logic for creating a new user in the system.
 * It includes validation, password hashing, and event publishing.
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
 *     .build();
 * 
 * CreateUserResponse response = createUserUseCase.execute(request);
 * }</pre>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see CreateUserRequest
 * @see CreateUserResponse
 * @see UserRepository
 * @see UserDomainService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateUserUseCase {

  private final UserRepository userRepository;
  private final UserDomainService userDomainService;
  private final PasswordEncoder passwordEncoder;
  private final SecurityAuditService securityAuditService;
  private final PerformanceMonitoringService performanceMonitoringService;

  /**
   * Executes the create user use case.
   *
   * @param request the create user request
   * @return the user response
   * @throws IllegalArgumentException if request is null or invalid
   * @throws UserAlreadyExistsException if user already exists
   */
  public CreateUserResponse execute(CreateUserRequest request) {
    long startTime = System.currentTimeMillis();
    
    try {
      // Validate request
      validateRequest(request);
      
      // Check if user already exists
      if (userRepository.existsByUsername(request.getUsername())) {
        throw new UserAlreadyExistsException("User with username " + request.getUsername() + " already exists", null);
      }
      
      if (userRepository.existsByEmail(request.getEmail())) {
        throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists", null);
      }
      
      // Hash password
      String hashedPassword = passwordEncoder.encode(request.getPassword());
      
      // Create user domain entity
      User user = new User(
          request.getUsername(),
          request.getEmail(),
          hashedPassword,
          request.getFirstName(),
          request.getLastName(),
          request.getRole()
      );
      
      // Set optional fields
      if (request.getProfileImageUrl() != null) {
        user.updateProfileImageUrl(request.getProfileImageUrl());
      }
      
      // Save user
      User savedUser = userRepository.save(user);
      
      // Record successful operation
      long executionTime = System.currentTimeMillis() - startTime;
      securityAuditService.logUserCreated(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
      performanceMonitoringService.recordUserCreationTime(executionTime);
      
      log.info("User created successfully: {}", savedUser.getUsername());
      
      // Convert to response DTO
      return CreateUserResponse.fromUser(savedUser);
      
    } catch (UserAlreadyExistsException e) {
      // Record failed attempt in security audit
      securityAuditService.logUserCreationFailed(request.getUsername(), request.getEmail(), e.getMessage());
      
      // Record performance metrics for failed operation
      long executionTime = System.currentTimeMillis() - startTime;
      performanceMonitoringService.recordUserCreationFailure(executionTime);
      
      throw e;
      
    } catch (Exception e) {
      // Record unexpected error in security audit
      securityAuditService.logUserCreationFailed(request.getUsername(), request.getEmail(), "Unexpected error: " + e.getMessage());
      
      // Record performance metrics for failed operation
      long executionTime = System.currentTimeMillis() - startTime;
      performanceMonitoringService.recordUserCreationFailure(executionTime);
      
      log.error("Unexpected error during user creation for username: {}", request.getUsername(), e);
      throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
    }
  }

  /**
   * Validates the create user request.
   *
   * @param request the request to validate
   * @throws IllegalArgumentException if request is null or invalid
   */
  private void validateRequest(CreateUserRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Create user request cannot be null");
    }
    
    if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    
    if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
      throw new IllegalArgumentException("Email cannot be null or empty");
    }
    
    if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
      throw new IllegalArgumentException("Password cannot be null or empty");
    }
    
    if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
      throw new IllegalArgumentException("First name cannot be null or empty");
    }
    
    if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
      throw new IllegalArgumentException("Last name cannot be null or empty");
    }
    
    if (request.getRole() == null) {
      throw new IllegalArgumentException("Role cannot be null");
    }
  }
}
