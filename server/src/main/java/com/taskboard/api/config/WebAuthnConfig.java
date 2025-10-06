package com.taskboard.api.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * Configuration for WebAuthn (Passkeys) authentication using Spring Security 6.5 DSL Provides
 * passwordless authentication using WebAuthn standard
 */
@Configuration
public class WebAuthnConfig {

  @Value("${webauthn.rp.id:localhost}")
  private String rpId;

  @Value("${webauthn.rp.name:TaskBoard Pro}")
  private String rpName;

  @Value("${webauthn.rp.origins:http://localhost:4200}")
  private List<String> allowedOrigins;

  /** Success handler for WebAuthn authentication */
  @Bean
  public AuthenticationSuccessHandler webAuthnSuccessHandler() {
    SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
    handler.setDefaultTargetUrl("/api/auth/success");
    handler.setAlwaysUseDefaultTargetUrl(true);
    return handler;
  }

  /** Failure handler for WebAuthn authentication */
  @Bean
  public AuthenticationFailureHandler webAuthnFailureHandler() {
    SimpleUrlAuthenticationFailureHandler handler = new SimpleUrlAuthenticationFailureHandler();
    handler.setDefaultFailureUrl("/api/auth/failure");
    return handler;
  }

  /** Get Relying Party ID */
  public String getRpId() {
    return rpId;
  }

  /** Get Relying Party Name */
  public String getRpName() {
    return rpName;
  }

  /** Get allowed origins */
  public List<String> getAllowedOrigins() {
    return allowedOrigins;
  }
}
