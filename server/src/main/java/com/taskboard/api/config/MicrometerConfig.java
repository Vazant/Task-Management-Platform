package com.taskboard.api.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.DefaultTracingObservationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Micrometer configuration with automatic context propagation Implements Spring Security 6.5
 * Micrometer integration features
 */
@Configuration
public class MicrometerConfig {

  /** Prometheus meter registry with custom configuration */
  @Bean
  @Primary
  public MeterRegistry meterRegistry() {
    PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    // Configure meter filters for better observability
    registry.config().meterFilter(MeterFilter.maxExpected("http.server.requests", 10_000L));

    registry.config().meterFilter(MeterFilter.maxExpected("http.client.requests", 10_000L));

    return registry;
  }

  /** Observation registry with tracing support */
  @Bean
  public ObservationRegistry observationRegistry(Tracer tracer) {
    ObservationRegistry registry = ObservationRegistry.create();

    // Add tracing observation handlers for automatic context propagation
    registry.observationConfig().observationHandler(new DefaultTracingObservationHandler(tracer));

    return registry;
  }

  /** Custom meter filter for security-related metrics */
  @Bean
  public MeterFilter securityMetricsFilter() {
    return MeterFilter.acceptNameStartsWith("security");
  }

  /** Custom meter filter for WebAuthn metrics */
  @Bean
  public MeterFilter webAuthnMetricsFilter() {
    return MeterFilter.acceptNameStartsWith("webauthn");
  }

  /** Custom meter filter for DPoP metrics */
  @Bean
  public MeterFilter dpopMetricsFilter() {
    return MeterFilter.acceptNameStartsWith("dpop");
  }

  /** Custom meter filter for One-Time Token metrics */
  @Bean
  public MeterFilter oneTimeTokenMetricsFilter() {
    return MeterFilter.acceptNameStartsWith("one.time.token");
  }
}
