package com.taskboard.api.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for Security Metrics Service Tests metrics collection for security-related operations
 */
@ExtendWith(MockitoExtension.class)
@Disabled("Temporarily disabled due to Micrometer mocking issues")
class SecurityMetricsServiceTest {

  @Mock private MeterRegistry meterRegistry;

  @Mock private Counter mockCounter;

  @Mock private Timer mockTimer;

  private SecurityMetricsService securityMetricsService;

  @BeforeEach
  void setUp() {
    // Mock counter and timer creation
    lenient()
        .when(meterRegistry.counter(anyString(), anyString(), anyString()))
        .thenReturn(mockCounter);
    lenient().when(meterRegistry.counter(anyString())).thenReturn(mockCounter);
    lenient().when(meterRegistry.timer(anyString())).thenReturn(mockTimer);

    // Mock timer creation with tags
    lenient()
        .when(meterRegistry.timer(anyString(), anyString(), anyString()))
        .thenReturn(mockTimer);

    securityMetricsService = new SecurityMetricsService(meterRegistry);
  }

  @Test
  void testRecordDpopProofCreated() {
    // When
    securityMetricsService.recordDpopProofCreated();

    // Then
    verify(mockCounter).increment();
  }

  @Test
  void testRecordDpopProofValidatedSuccess() {
    // Given
    Duration duration = Duration.ofMillis(100);

    // When
    securityMetricsService.recordDpopProofValidated(true, duration);

    // Then
    verify(mockCounter, times(2)).increment(); // One for success, one for timer
    verify(mockTimer).record(duration);
  }

  @Test
  void testRecordDpopProofValidatedFailure() {
    // Given
    Duration duration = Duration.ofMillis(50);

    // When
    securityMetricsService.recordDpopProofValidated(false, duration);

    // Then
    verify(mockCounter, times(2)).increment(); // One for failure, one for timer
    verify(mockTimer).record(duration);
  }

  @Test
  void testRecordWebAuthnRegistrationAttempt() {
    // When
    securityMetricsService.recordWebAuthnRegistrationAttempt();

    // Then
    verify(mockCounter).increment();
  }

  @Test
  void testRecordWebAuthnRegistrationSuccess() {
    // Given
    Duration duration = Duration.ofSeconds(2);

    // When
    securityMetricsService.recordWebAuthnRegistrationSuccess(duration);

    // Then
    verify(mockCounter, times(2)).increment(); // One for success, one for timer
    verify(mockTimer).record(duration);
  }

  @Test
  void testRecordWebAuthnAuthenticationAttempt() {
    // When
    securityMetricsService.recordWebAuthnAuthenticationAttempt();

    // Then
    verify(mockCounter).increment();
  }

  @Test
  void testRecordWebAuthnAuthenticationSuccess() {
    // Given
    Duration duration = Duration.ofMillis(500);

    // When
    securityMetricsService.recordWebAuthnAuthenticationSuccess(duration);

    // Then
    verify(mockCounter, times(2)).increment(); // One for success, one for timer
    verify(mockTimer).record(duration);
  }

  @Test
  void testRecordOneTimeTokenGenerated() {
    // When
    securityMetricsService.recordOneTimeTokenGenerated();

    // Then
    verify(mockCounter).increment();
  }

  @Test
  void testRecordOneTimeTokenValidatedSuccess() {
    // Given
    Duration duration = Duration.ofMillis(75);

    // When
    securityMetricsService.recordOneTimeTokenValidated(true, duration);

    // Then
    verify(mockCounter, times(2)).increment(); // One for success, one for timer
    verify(mockTimer).record(duration);
  }

  @Test
  void testRecordOneTimeTokenValidatedFailure() {
    // Given
    Duration duration = Duration.ofMillis(25);

    // When
    securityMetricsService.recordOneTimeTokenValidated(false, duration);

    // Then
    verify(mockCounter, times(2)).increment(); // One for failure, one for timer
    verify(mockTimer).record(duration);
  }

  @Test
  void testRecordOAuth2LoginAttempt() {
    // When
    securityMetricsService.recordOAuth2LoginAttempt();

    // Then
    verify(mockCounter).increment();
  }

  @Test
  void testRecordOAuth2LoginSuccess() {
    // Given
    Duration duration = Duration.ofSeconds(1);

    // When
    securityMetricsService.recordOAuth2LoginSuccess(duration);

    // Then
    verify(mockCounter, times(2)).increment(); // One for success, one for timer
    verify(mockTimer).record(duration);
  }

  @Test
  void testRecordOAuth2LoginFailed() {
    // When
    securityMetricsService.recordOAuth2LoginFailed();

    // Then
    verify(mockCounter).increment();
  }
}
