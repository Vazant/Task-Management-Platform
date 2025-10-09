package com.taskboard.userservice.infrastructure.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT configuration for User Service.
 * 
 * <p>This configuration class sets up JWT-related beans and configuration
 * for token generation, validation, and management.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>JWT secret key configuration</li>
 *   <li>Token expiration settings</li>
 *   <li>Key generation and validation</li>
 * </ul>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class JwtConfig {
    
    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;
    
    @Value("${spring.security.jwt.expiration}")
    private Long jwtExpiration;
    
    @Value("${spring.security.jwt.refresh-expiration}")
    private Long jwtRefreshExpiration;
    
    /**
     * Creates the JWT secret key from the configured secret string.
     * 
     * <p>This method converts the configured JWT secret string into a
     * SecretKey object that can be used for signing and verifying JWT tokens.</p>
     * 
     * @return JWT secret key
     * @throws IllegalArgumentException if secret is too short or invalid
     */
    @Bean
    public SecretKey jwtSecretKey() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }
        
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Gets the JWT expiration time in milliseconds.
     * 
     * @return JWT expiration time
     */
    public Long getJwtExpiration() {
        return jwtExpiration * 1000; // Convert seconds to milliseconds
    }
    
    /**
     * Gets the JWT refresh token expiration time in milliseconds.
     * 
     * @return JWT refresh token expiration time
     */
    public Long getJwtRefreshExpiration() {
        return jwtRefreshExpiration * 1000; // Convert seconds to milliseconds
    }
    
    /**
     * Gets the JWT secret string.
     * 
     * @return JWT secret string
     */
    public String getJwtSecret() {
        return jwtSecret;
    }
}
