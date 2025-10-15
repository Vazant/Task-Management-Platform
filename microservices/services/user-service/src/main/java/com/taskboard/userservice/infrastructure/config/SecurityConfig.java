package com.taskboard.userservice.infrastructure.config;

import com.taskboard.userservice.infrastructure.security.JwtAccessDeniedHandler;
import com.taskboard.userservice.infrastructure.security.JwtAuthenticationEntryPoint;
import com.taskboard.userservice.infrastructure.security.JwtRequestFilter;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security configuration for User Service.
 *
 * <p>This configuration class sets up security for the User Service microservice, including JWT
 * authentication, authorization, CORS, and security filters.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>JWT-based stateless authentication
 *   <li>Role-based access control (RBAC)
 *   <li>CORS configuration
 *   <li>Password encoding with BCrypt
 *   <li>Security filter chain
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@Profile("!test") // Exclude this configuration from the "test" profile
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtRequestFilter jwtRequestFilter;

  /**
   * Constructs a new SecurityConfig with required dependencies.
   *
   * @param jwtAuthenticationEntryPoint JWT authentication entry point
   * @param jwtRequestFilter JWT request filter
   */
  public SecurityConfig(
      JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtRequestFilter jwtRequestFilter) {
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    this.jwtRequestFilter = jwtRequestFilter;
  }

  /**
   * Configures the security filter chain.
   *
   * <p>This method sets up the security configuration including:
   *
   * <ul>
   *   <li>CSRF protection (disabled for stateless API)
   *   <li>Session management (stateless)
   *   <li>CORS configuration
   *   <li>Authorization rules
   *   <li>Exception handling
   *   <li>JWT filter integration
   * </ul>
   *
   * @param http HttpSecurity configuration
   * @return configured SecurityFilterChain
   * @throws Exception if configuration fails
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // Disable CSRF for stateless API
        .csrf(AbstractHttpConfigurer::disable)

        // Configure CORS
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))

        // Configure session management as stateless
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Configure authorization rules
        .authorizeHttpRequests(
            authz ->
                authz
                    // Public endpoints
                    .requestMatchers("/api/auth/**")
                    .permitAll()
                    .requestMatchers("/api/health/**")
                    .permitAll()
                    .requestMatchers("/actuator/health/**")
                    .permitAll()
                    .requestMatchers("/actuator/info")
                    .permitAll()
                    .requestMatchers("/swagger-ui/**")
                    .permitAll()
                    .requestMatchers("/api-docs/**")
                    .permitAll()
                    .requestMatchers("/v3/api-docs/**")
                    .permitAll()

                    // User management endpoints
                    .requestMatchers("/api/users", "POST")
                    .permitAll() // Registration
                    .requestMatchers("/api/users/{id}", "GET")
                    .hasAnyRole("USER", "ADMIN", "SUPER_ADMIN")
                    .requestMatchers("/api/users/{id}", "PUT")
                    .hasAnyRole("USER", "ADMIN", "SUPER_ADMIN")
                    .requestMatchers("/api/users/{id}", "DELETE")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers("/api/users", "GET")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")

                    // Profile management
                    .requestMatchers("/api/users/{id}/profile", "GET")
                    .hasAnyRole("USER", "ADMIN", "SUPER_ADMIN")
                    .requestMatchers("/api/users/{id}/profile", "PUT")
                    .hasAnyRole("USER", "ADMIN", "SUPER_ADMIN")
                    .requestMatchers("/api/users/{id}/roles", "GET")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers("/api/users/{id}/roles", "PUT")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")

                    // Admin endpoints
                    .requestMatchers("/api/admin/**")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")

                    // All other requests require authentication
                    .anyRequest()
                    .authenticated())

        // Configure exception handling
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(new JwtAccessDeniedHandler()))

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * Creates and configures the authentication manager.
   *
   * @param authConfig authentication configuration
   * @return configured authentication manager
   * @throws Exception if configuration fails
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }

  /**
   * Creates and configures the password encoder.
   *
   * <p>Uses BCrypt with strength 12 for secure password hashing. This strength provides a good
   * balance between security and performance.
   *
   * @return configured password encoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  /**
   * Creates and configures CORS configuration source.
   *
   * <p>This configuration allows cross-origin requests from specified origins and configures
   * allowed methods, headers, and credentials.
   *
   * @return configured CORS configuration source
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Allow specific origins (configured via properties)
    configuration.setAllowedOriginPatterns(
        List.of("http://localhost:3000", "http://localhost:4200", "https://*.taskboard.com"));

    // Allow specific methods
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

    // Allow specific headers
    configuration.setAllowedHeaders(
        Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"));

    // Allow credentials
    configuration.setAllowCredentials(true);

    // Cache preflight response for 1 hour
    configuration.setMaxAge(3600L);

    // Apply configuration to all paths
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);

    return source;
  }
}
