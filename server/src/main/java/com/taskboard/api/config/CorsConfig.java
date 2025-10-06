package com.taskboard.api.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@SuppressWarnings("checkstyle:DesignForExtension")
public class CorsConfig {

  private static final long MAX_AGE_SECONDS = 3600L;

  @Value("${cors.allowed-origins:http://localhost:4200}")
  private String allowedOrigins;

  @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
  private String allowedMethods;

  @Value("${cors.allowed-headers:*}")
  private String allowedHeaders;

  @Value("${cors.allow-credentials:true}")
  private boolean allowCredentials;

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();

    // Используем setAllowedOrigins вместо setAllowedOriginPatterns
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
    configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));

    // Явно указываем необходимые заголовки вместо *
    configuration.setAllowedHeaders(
        Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-Content-Type-Options",
            "X-Frame-Options",
            "X-XSS-Protection",
            "Referrer-Policy",
            "X-CSRF-TOKEN"));

    configuration.setAllowCredentials(allowCredentials);
    configuration.setMaxAge(MAX_AGE_SECONDS); // Кэширование preflight запросов на 1 час

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
