package com.taskboard.userservice.infrastructure.monitoring;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for SecurityAuditService. */
@DisplayName("SecurityAuditService Tests")
class SecurityAuditServiceTest {

  private MeterRegistry meterRegistry;
  private SecurityAuditService securityAuditService;

  @BeforeEach
  void setUp() {
    meterRegistry = new SimpleMeterRegistry();
    securityAuditService = new SecurityAuditService(meterRegistry);
  }

  @Test
  @DisplayName("Should record successful login attempt")
  void shouldRecordSuccessfulLoginAttempt() {
    // Given
    String username = "testuser";
    String ipAddress = "192.168.1.1";
    boolean success = true;

    // When
    securityAuditService.recordLoginAttempt(username, ipAddress, success);

    // Then
    assertThat(meterRegistry.find("security.login.attempts.total").counter().count())
        .isEqualTo(1.0);
    assertThat(meterRegistry.find("security.login.success.total").counter().count()).isEqualTo(1.0);
    assertThat(meterRegistry.find("security.login.failures.total").counter().count())
        .isEqualTo(0.0);
  }

  @Test
  @DisplayName("Should record failed login attempt")
  void shouldRecordFailedLoginAttempt() {
    // Given
    String username = "testuser";
    String ipAddress = "192.168.1.1";
    boolean success = false;

    // When
    securityAuditService.recordLoginAttempt(username, ipAddress, success);

    // Then
    assertThat(meterRegistry.find("security.login.attempts.total").counter().count())
        .isEqualTo(1.0);
    assertThat(meterRegistry.find("security.login.success.total").counter().count()).isEqualTo(0.0);
    assertThat(meterRegistry.find("security.login.failures.total").counter().count())
        .isEqualTo(1.0);
    assertThat(securityAuditService.getFailedAttemptsForIp(ipAddress)).isEqualTo(1);
  }

  @Test
  @DisplayName("Should record password change")
  void shouldRecordPasswordChange() {
    // Given
    String username = "testuser";
    String ipAddress = "192.168.1.1";

    // When
    securityAuditService.recordPasswordChange(username, ipAddress);

    // Then
    assertThat(meterRegistry.find("security.password.changes.total").counter().count())
        .isEqualTo(1.0);
  }

  @Test
  @DisplayName("Should record account lockout")
  void shouldRecordAccountLockout() {
    // Given
    String username = "testuser";
    String ipAddress = "192.168.1.1";
    String reason = "Multiple failed attempts";

    // When
    securityAuditService.recordAccountLockout(username, ipAddress, reason);

    // Then
    assertThat(meterRegistry.find("security.account.lockouts.total").counter().count())
        .isEqualTo(1.0);
  }

  @Test
  @DisplayName("Should record suspicious activity")
  void shouldRecordSuspiciousActivity() {
    // Given
    String activityType = "BRUTE_FORCE_ATTEMPT";
    String username = "testuser";
    String ipAddress = "192.168.1.1";
    String details = "Multiple failed login attempts";

    // When
    securityAuditService.recordSuspiciousActivity(activityType, username, ipAddress, details);

    // Then
    assertThat(meterRegistry.find("security.suspicious.activity.total").counter().count())
        .isEqualTo(1.0);
  }

  @Test
  @DisplayName("Should record valid JWT token")
  void shouldRecordValidJwtToken() {
    // Given
    boolean tokenValid = true;
    String ipAddress = "192.168.1.1";

    // When
    securityAuditService.recordTokenValidation(tokenValid, ipAddress);

    // Then
    // Valid token validation doesn't increment any counters, just logs
    assertThat(meterRegistry.find("security.suspicious.activity.total").counter().count())
        .isEqualTo(0.0);
  }

  @Test
  @DisplayName("Should record invalid JWT token")
  void shouldRecordInvalidJwtToken() {
    // Given
    boolean tokenValid = false;
    String ipAddress = "192.168.1.1";

    // When
    securityAuditService.recordTokenValidation(tokenValid, ipAddress);

    // Then
    assertThat(meterRegistry.find("security.suspicious.activity.total").counter().count())
        .isEqualTo(1.0);
  }

  @Test
  @DisplayName("Should record authorization failure")
  void shouldRecordAuthorizationFailure() {
    // Given
    String username = "testuser";
    String resource = "/api/users/123";
    String action = "DELETE";
    String ipAddress = "192.168.1.1";

    // When
    securityAuditService.recordAuthorizationFailure(username, resource, action, ipAddress);

    // Then
    assertThat(meterRegistry.find("security.suspicious.activity.total").counter().count())
        .isEqualTo(1.0);
  }

  @Test
  @DisplayName("Should track multiple failed attempts from same IP")
  void shouldTrackMultipleFailedAttemptsFromSameIp() {
    // Given
    String username = "testuser";
    String ipAddress = "192.168.1.1";

    // When
    securityAuditService.recordLoginAttempt(username, ipAddress, false);
    securityAuditService.recordLoginAttempt(username, ipAddress, false);
    securityAuditService.recordLoginAttempt(username, ipAddress, false);

    // Then
    assertThat(meterRegistry.find("security.login.failures.total").counter().count())
        .isEqualTo(3.0);
    assertThat(securityAuditService.getFailedAttemptsForIp(ipAddress)).isEqualTo(3);
  }

  @Test
  @DisplayName("Should accumulate multiple metrics")
  void shouldAccumulateMultipleMetrics() {
    // Given
    String username = "testuser";
    String ipAddress = "192.168.1.1";

    // When
    securityAuditService.recordLoginAttempt(username, ipAddress, true);
    securityAuditService.recordPasswordChange(username, ipAddress);
    securityAuditService.recordSuspiciousActivity(
        "TEST_ACTIVITY", username, ipAddress, "Test details");

    // Then
    assertThat(meterRegistry.find("security.login.attempts.total").counter().count())
        .isEqualTo(1.0);
    assertThat(meterRegistry.find("security.password.changes.total").counter().count())
        .isEqualTo(1.0);
    assertThat(meterRegistry.find("security.suspicious.activity.total").counter().count())
        .isEqualTo(1.0);
  }
}
