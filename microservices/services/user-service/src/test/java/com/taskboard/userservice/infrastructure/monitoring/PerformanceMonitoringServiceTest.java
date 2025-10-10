package com.taskboard.userservice.infrastructure.monitoring;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for PerformanceMonitoringService. */
@DisplayName("PerformanceMonitoringService Tests")
class PerformanceMonitoringServiceTest {

  private MeterRegistry meterRegistry;
  private PerformanceMonitoringService performanceMonitoringService;

  @BeforeEach
  void setUp() {
    meterRegistry = new SimpleMeterRegistry();
    performanceMonitoringService = new PerformanceMonitoringService(meterRegistry);
  }

  @Test
  @DisplayName("Should record user creation metrics")
  void shouldRecordUserCreationMetrics() {
    // Given
    Duration duration = Duration.ofMillis(150);
    boolean success = true;

    // When
    performanceMonitoringService.recordUserCreation(duration, success);

    // Then
    assertThat(meterRegistry.find("user.creation.total").counter().count()).isEqualTo(1.0);
    assertThat(
            meterRegistry
                .find("user.creation.duration")
                .timer()
                .totalTime(java.util.concurrent.TimeUnit.MILLISECONDS))
        .isEqualTo(150.0);
  }

  @Test
  @DisplayName("Should record user authentication metrics")
  void shouldRecordUserAuthenticationMetrics() {
    // Given
    Duration duration = Duration.ofMillis(100);
    boolean success = true;

    // When
    performanceMonitoringService.recordUserAuthentication(duration, success);

    // Then
    assertThat(meterRegistry.find("user.authentication.success.total").counter().count())
        .isEqualTo(1.0);
    assertThat(
            meterRegistry
                .find("user.authentication.duration")
                .timer()
                .totalTime(java.util.concurrent.TimeUnit.MILLISECONDS))
        .isEqualTo(100.0);
  }

  @Test
  @DisplayName("Should record failed user authentication metrics")
  void shouldRecordFailedUserAuthenticationMetrics() {
    // Given
    Duration duration = Duration.ofMillis(50);
    boolean success = false;

    // When
    performanceMonitoringService.recordUserAuthentication(duration, success);

    // Then
    assertThat(meterRegistry.find("user.authentication.failure.total").counter().count())
        .isEqualTo(1.0);
    assertThat(meterRegistry.find("user.authentication.success.total").counter().count())
        .isEqualTo(0.0);
  }

  @Test
  @DisplayName("Should record user update metrics")
  void shouldRecordUserUpdateMetrics() {
    // Given
    Duration duration = Duration.ofMillis(200);
    boolean success = true;

    // When
    performanceMonitoringService.recordUserUpdate(duration, success);

    // Then
    assertThat(meterRegistry.find("user.update.total").counter().count()).isEqualTo(1.0);
    assertThat(
            meterRegistry
                .find("user.update.duration")
                .timer()
                .totalTime(java.util.concurrent.TimeUnit.MILLISECONDS))
        .isEqualTo(200.0);
  }

  @Test
  @DisplayName("Should record user deletion metrics")
  void shouldRecordUserDeletionMetrics() {
    // Given
    Duration duration = Duration.ofMillis(75);
    boolean success = true;

    // When
    performanceMonitoringService.recordUserDeletion(duration, success);

    // Then
    assertThat(meterRegistry.find("user.deletion.total").counter().count()).isEqualTo(1.0);
    assertThat(
            meterRegistry
                .find("user.deletion.duration")
                .timer()
                .totalTime(java.util.concurrent.TimeUnit.MILLISECONDS))
        .isEqualTo(75.0);
  }

  @Test
  @DisplayName("Should record custom metrics")
  void shouldRecordCustomMetrics() {
    // Given
    String metricName = "custom.metric";
    double value = 42.5;
    String[] tags = {"tag1", "value1"};

    // When
    performanceMonitoringService.recordCustomMetric(metricName, value, tags);

    // Then
    assertThat(meterRegistry.find(metricName).gauge().value()).isEqualTo(value);
  }

  @Test
  @DisplayName("Should record error metrics")
  void shouldRecordErrorMetrics() {
    // Given
    String errorType = "VALIDATION_ERROR";
    String errorMessage = "Invalid input data";

    // When
    performanceMonitoringService.recordError(errorType, errorMessage);

    // Then
    assertThat(meterRegistry.find("user.service.errors.total").counter().count()).isEqualTo(1.0);
  }

  @Test
  @DisplayName("Should accumulate multiple metrics")
  void shouldAccumulateMultipleMetrics() {
    // Given
    Duration duration1 = Duration.ofMillis(100);
    Duration duration2 = Duration.ofMillis(200);

    // When
    performanceMonitoringService.recordUserCreation(duration1, true);
    performanceMonitoringService.recordUserCreation(duration2, true);

    // Then
    assertThat(meterRegistry.find("user.creation.total").counter().count()).isEqualTo(2.0);
    assertThat(
            meterRegistry
                .find("user.creation.duration")
                .timer()
                .totalTime(java.util.concurrent.TimeUnit.MILLISECONDS))
        .isEqualTo(300.0);
  }
}
