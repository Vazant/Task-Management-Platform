package com.taskboard.api.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for collecting security-related metrics Integrates with Micrometer for observability and
 * monitoring
 */
@Service
@Slf4j
public class SecurityMetricsService {

  private final MeterRegistry meterRegistry;

  // DPoP metrics
  private final Counter dpopProofCreatedCounter;
  private final Counter dpopProofValidatedCounter;
  private final Counter dpopProofValidationFailedCounter;
  private final Timer dpopValidationTimer;

  // WebAuthn metrics
  private final Counter webAuthnRegistrationAttemptsCounter;
  private final Counter webAuthnRegistrationSuccessCounter;
  private final Counter webAuthnAuthenticationAttemptsCounter;
  private final Counter webAuthnAuthenticationSuccessCounter;
  private final Timer webAuthnRegistrationTimer;
  private final Timer webAuthnAuthenticationTimer;

  // One-Time Token metrics
  private final Counter ottGeneratedCounter;
  private final Counter ottValidatedCounter;
  private final Counter ottValidationFailedCounter;
  private final Timer ottValidationTimer;

  // OAuth2 metrics
  private final Counter oauth2LoginAttemptsCounter;
  private final Counter oauth2LoginSuccessCounter;
  private final Counter oauth2LoginFailedCounter;
  private final Timer oauth2LoginTimer;

  @Autowired
  public SecurityMetricsService(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;

    // Initialize DPoP metrics
    this.dpopProofCreatedCounter =
        Counter.builder("dpop.proof.created")
            .description("Number of DPoP proof tokens created")
            .register(meterRegistry);

    this.dpopProofValidatedCounter =
        Counter.builder("dpop.proof.validated")
            .description("Number of DPoP proof tokens successfully validated")
            .register(meterRegistry);

    this.dpopProofValidationFailedCounter =
        Counter.builder("dpop.proof.validation.failed")
            .description("Number of DPoP proof tokens that failed validation")
            .register(meterRegistry);

    this.dpopValidationTimer =
        Timer.builder("dpop.validation.duration")
            .description("Time taken to validate DPoP proof tokens")
            .register(meterRegistry);

    // Initialize WebAuthn metrics
    this.webAuthnRegistrationAttemptsCounter =
        Counter.builder("webauthn.registration.attempts")
            .description("Number of WebAuthn registration attempts")
            .register(meterRegistry);

    this.webAuthnRegistrationSuccessCounter =
        Counter.builder("webauthn.registration.success")
            .description("Number of successful WebAuthn registrations")
            .register(meterRegistry);

    this.webAuthnAuthenticationAttemptsCounter =
        Counter.builder("webauthn.authentication.attempts")
            .description("Number of WebAuthn authentication attempts")
            .register(meterRegistry);

    this.webAuthnAuthenticationSuccessCounter =
        Counter.builder("webauthn.authentication.success")
            .description("Number of successful WebAuthn authentications")
            .register(meterRegistry);

    this.webAuthnRegistrationTimer =
        Timer.builder("webauthn.registration.duration")
            .description("Time taken for WebAuthn registration")
            .register(meterRegistry);

    this.webAuthnAuthenticationTimer =
        Timer.builder("webauthn.authentication.duration")
            .description("Time taken for WebAuthn authentication")
            .register(meterRegistry);

    // Initialize One-Time Token metrics
    this.ottGeneratedCounter =
        Counter.builder("one.time.token.generated")
            .description("Number of one-time tokens generated")
            .register(meterRegistry);

    this.ottValidatedCounter =
        Counter.builder("one.time.token.validated")
            .description("Number of one-time tokens successfully validated")
            .register(meterRegistry);

    this.ottValidationFailedCounter =
        Counter.builder("one.time.token.validation.failed")
            .description("Number of one-time tokens that failed validation")
            .register(meterRegistry);

    this.ottValidationTimer =
        Timer.builder("one.time.token.validation.duration")
            .description("Time taken to validate one-time tokens")
            .register(meterRegistry);

    // Initialize OAuth2 metrics
    this.oauth2LoginAttemptsCounter =
        Counter.builder("oauth2.login.attempts")
            .description("Number of OAuth2 login attempts")
            .register(meterRegistry);

    this.oauth2LoginSuccessCounter =
        Counter.builder("oauth2.login.success")
            .description("Number of successful OAuth2 logins")
            .register(meterRegistry);

    this.oauth2LoginFailedCounter =
        Counter.builder("oauth2.login.failed")
            .description("Number of failed OAuth2 logins")
            .register(meterRegistry);

    this.oauth2LoginTimer =
        Timer.builder("oauth2.login.duration")
            .description("Time taken for OAuth2 login")
            .register(meterRegistry);
  }

  // DPoP metrics methods
  public void recordDpopProofCreated() {
    dpopProofCreatedCounter.increment();
    log.debug("DPoP proof created metric recorded");
  }

  public void recordDpopProofValidated(boolean success, Duration duration) {
    if (success) {
      dpopProofValidatedCounter.increment();
    } else {
      dpopProofValidationFailedCounter.increment();
    }
    dpopValidationTimer.record(duration);
    log.debug("DPoP proof validation metric recorded: success={}, duration={}", success, duration);
  }

  // WebAuthn metrics methods
  public void recordWebAuthnRegistrationAttempt() {
    webAuthnRegistrationAttemptsCounter.increment();
    log.debug("WebAuthn registration attempt metric recorded");
  }

  public void recordWebAuthnRegistrationSuccess(Duration duration) {
    webAuthnRegistrationSuccessCounter.increment();
    webAuthnRegistrationTimer.record(duration);
    log.debug("WebAuthn registration success metric recorded, duration={}", duration);
  }

  public void recordWebAuthnAuthenticationAttempt() {
    webAuthnAuthenticationAttemptsCounter.increment();
    log.debug("WebAuthn authentication attempt metric recorded");
  }

  public void recordWebAuthnAuthenticationSuccess(Duration duration) {
    webAuthnAuthenticationSuccessCounter.increment();
    webAuthnAuthenticationTimer.record(duration);
    log.debug("WebAuthn authentication success metric recorded, duration={}", duration);
  }

  // One-Time Token metrics methods
  public void recordOneTimeTokenGenerated() {
    ottGeneratedCounter.increment();
    log.debug("One-time token generated metric recorded");
  }

  public void recordOneTimeTokenValidated(boolean success, Duration duration) {
    if (success) {
      ottValidatedCounter.increment();
    } else {
      ottValidationFailedCounter.increment();
    }
    ottValidationTimer.record(duration);
    log.debug(
        "One-time token validation metric recorded: success={}, duration={}", success, duration);
  }

  // OAuth2 metrics methods
  public void recordOAuth2LoginAttempt() {
    oauth2LoginAttemptsCounter.increment();
    log.debug("OAuth2 login attempt metric recorded");
  }

  public void recordOAuth2LoginSuccess(Duration duration) {
    oauth2LoginSuccessCounter.increment();
    oauth2LoginTimer.record(duration);
    log.debug("OAuth2 login success metric recorded, duration={}", duration);
  }

  public void recordOAuth2LoginFailed() {
    oauth2LoginFailedCounter.increment();
    log.debug("OAuth2 login failed metric recorded");
  }
}
