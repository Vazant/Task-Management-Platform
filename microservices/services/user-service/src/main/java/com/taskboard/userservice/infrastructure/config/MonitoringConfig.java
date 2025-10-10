package com.taskboard.userservice.infrastructure.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for monitoring and metrics. Sets up Micrometer metrics, Prometheus
 * integration, and custom metrics.
 */
@Configuration
public class MonitoringConfig {

  /**
   * Enables @Timed annotation for method-level timing metrics.
   *
   * @param registry The meter registry.
   * @return A TimedAspect instance.
   */
  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }

  /**
   * Customizes the meter registry with application-specific tags.
   *
   * @return A MeterRegistryCustomizer.
   */
  @Bean
  public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry ->
        registry
            .config()
            .commonTags(
                "application", "user-service",
                "version", "1.0.0",
                "environment", getEnvironment());
  }

  /**
   * Gets the current environment from system properties or environment variables.
   *
   * @return The environment name.
   */
  private String getEnvironment() {
    String env = System.getProperty("spring.profiles.active");
    if (env == null) {
      env = System.getenv("SPRING_PROFILES_ACTIVE");
    }
    return env != null ? env : "default";
  }
}
