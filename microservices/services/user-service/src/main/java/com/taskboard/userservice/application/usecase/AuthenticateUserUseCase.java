package com.taskboard.userservice.application.usecase;

import com.taskboard.userservice.application.dto.AuthenticateUserRequest;
import com.taskboard.userservice.application.dto.AuthenticateUserResponse;
import com.taskboard.userservice.domain.exception.UserNotFoundException;
import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserStatus;
import com.taskboard.userservice.domain.repository.UserRepository;
import com.taskboard.userservice.infrastructure.monitoring.PerformanceMonitoringService;
import com.taskboard.userservice.infrastructure.monitoring.SecurityAuditService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

/**
 * Use case for authenticating a user.
 *
 * <p>This use case handles the business logic for user authentication.
 * It includes validation, credential verification, and token generation.
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see AuthenticateUserRequest
 * @see AuthenticateUserResponse
 * @see UserRepository
 * @see SecurityAuditService
 * @see PerformanceMonitoringService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticateUserUseCase {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final SecurityAuditService securityAuditService;
  private final PerformanceMonitoringService performanceMonitoringService;
  private final Validator validator; // Injecting Jakarta Validator

  /**
   * Executes the user authentication operation.
   *
   * @param request the {@link AuthenticateUserRequest} containing user credentials
   * @return an {@link AuthenticateUserResponse} with authentication tokens and user info
   * @throws IllegalArgumentException if the request is null or invalid
   * @throws BadCredentialsException if authentication fails
   */
  public AuthenticateUserResponse execute(@Valid AuthenticateUserRequest request) {
    Instant start = Instant.now();
    String username = null;

    try {
      if (request == null) {
        throw new IllegalArgumentException("Authentication request cannot be null");
      }

      // Manual validation using Jakarta Validator
      Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);
      if (!violations.isEmpty()) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<AuthenticateUserRequest> violation : violations) {
          sb.append(violation.getMessage()).append("; ");
        }
        throw new IllegalArgumentException("Validation failed: " + sb.toString());
      }

      username = request.getUsername();
      log.info("Attempting to authenticate user: {}", username);

      // Find user by username
      User user = userRepository.findByUsername(username)
          .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

      // Check if user is active
      if (user.getStatus() != UserStatus.ACTIVE) {
        securityAuditService.logAuthenticationFailed(username, "User account is not active");
        throw new BadCredentialsException("User account is not active");
      }

      // Verify password
      if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
        securityAuditService.logAuthenticationFailed(username, "Invalid password");
        throw new BadCredentialsException("Invalid credentials");
      }

      // Generate tokens (simplified implementation)
      String accessToken = generateAccessToken(user);
      String refreshToken = generateRefreshToken(user);

      securityAuditService.logAuthenticationSuccess(user.getId(), username);
      log.info("Successfully authenticated user: {}", username);

      return AuthenticateUserResponse.builder()
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .user(AuthenticateUserResponse.AuthenticatedUserDto.fromDomain(user))
          .build();

    } catch (BadCredentialsException e) {
      handleAuthenticationFailure(username, "Invalid credentials", e, start);
      throw e;
    } catch (IllegalArgumentException e) {
      handleAuthenticationFailure(username, "Invalid request: " + e.getMessage(), e, start);
      throw e;
    } catch (Exception e) {
      handleAuthenticationFailure(username, "Unexpected error: " + e.getMessage(), e, start);
      log.error("Unexpected error during authentication for user: {}", username, e);
      throw new RuntimeException("Authentication failed", e);
    } finally {
      long duration = Duration.between(start, Instant.now()).toMillis();
      performanceMonitoringService.recordAuthenticationTime(duration);
    }
  }

  /**
   * Generates an access token for the authenticated user.
   *
   * @param user the authenticated user
   * @return the access token
   */
  private String generateAccessToken(User user) {
    // TODO: Implement JWT token generation
    // This is a simplified implementation
    return "access_token_" + user.getId() + "_" + System.currentTimeMillis();
  }

  /**
   * Generates a refresh token for the authenticated user.
   *
   * @param user the authenticated user
   * @return the refresh token
   */
  private String generateRefreshToken(User user) {
    // TODO: Implement refresh token generation
    // This is a simplified implementation
    return "refresh_token_" + user.getId() + "_" + System.currentTimeMillis();
  }

  /**
   * Handles authentication failure by logging and recording metrics.
   *
   * @param username the username
   * @param reason the reason for failure
   * @param e the exception that occurred
   * @param startTime the start time of the operation
   */
  private void handleAuthenticationFailure(String username, String reason, Exception e, Instant startTime) {
    securityAuditService.logAuthenticationFailed(username, reason);
    performanceMonitoringService.recordAuthenticationFailure(Duration.between(startTime, Instant.now()).toMillis());
    log.error("Authentication failed for user {}: {}", username, reason, e);
  }
}


