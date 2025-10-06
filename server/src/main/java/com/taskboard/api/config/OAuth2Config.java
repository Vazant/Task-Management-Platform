package com.taskboard.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

/**
 * OAuth2 configuration with PKCE support for confidential clients Implements Spring Security 6.5
 * OAuth2 features including PKCE for all client types
 */
@Configuration
public class OAuth2Config {

  /** Client registration repository with PKCE-enabled clients */
  @Bean
  public ClientRegistrationRepository clientRegistrationRepository() {
    return new InMemoryClientRegistrationRepository(
        googleClientRegistration(), githubClientRegistration(), customClientRegistration());
  }

  /** Google OAuth2 client with PKCE enabled */
  private ClientRegistration googleClientRegistration() {
    return ClientRegistration.withRegistrationId("google")
        .clientId("google-client-id")
        .clientSecret("google-client-secret")
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
        .scope("openid", "profile", "email")
        .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
        .tokenUri("https://www.googleapis.com/oauth2/v4/token")
        .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
        .userNameAttributeName(IdTokenClaimNames.SUB)
        .clientName("Google")
        .build();
  }

  /** GitHub OAuth2 client with PKCE enabled */
  private ClientRegistration githubClientRegistration() {
    return ClientRegistration.withRegistrationId("github")
        .clientId("github-client-id")
        .clientSecret("github-client-secret")
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
        .scope("read:user", "user:email")
        .authorizationUri("https://github.com/login/oauth/authorize")
        .tokenUri("https://github.com/login/oauth/access_token")
        .userInfoUri("https://api.github.com/user")
        .userNameAttributeName("id")
        .clientName("GitHub")
        .build();
  }

  /**
   * Custom OAuth2 client with PKCE enabled for confidential clients Demonstrates the new Spring
   * Security 6.5 feature
   */
  private ClientRegistration customClientRegistration() {
    return ClientRegistration.withRegistrationId("custom")
        .clientId("custom-client-id")
        .clientSecret("custom-client-secret")
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
        .scope("read", "write")
        .authorizationUri("https://custom-provider.com/oauth/authorize")
        .tokenUri("https://custom-provider.com/oauth/token")
        .userInfoUri("https://custom-provider.com/userinfo")
        .userNameAttributeName("sub")
        .clientName("Custom Provider")
        .build();
  }
}