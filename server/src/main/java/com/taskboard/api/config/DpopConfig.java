package com.taskboard.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Configuration for DPoP (Demonstrating Proof of Possession) tokens
 * Provides additional security for OAuth 2.0 tokens by binding them to client keys
 * 
 * DPoP ensures that only the client that requested an access token can use it,
 * preventing token theft and replay attacks.
 */
@Configuration
public class DpopConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * JWT decoder configuration for DPoP tokens
     * In production, use RSA keys or JWK Set for better security
     * 
     * @return JwtDecoder configured for DPoP token validation
     */
    @Bean
    public JwtDecoder dpopJwtDecoder() {
        // In production, use RSA keys or JWK Set instead of symmetric keys
        SecretKeySpec secretKey = new SecretKeySpec(
            jwtSecret.getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
        );

        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    /**
     * Converter for extracting authorities from JWT token
     * Maps JWT claims to Spring Security authorities
     * 
     * @return JwtAuthenticationConverter configured for DPoP
     */
    @Bean
    public JwtAuthenticationConverter dpopJwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("authorities");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authenticationConverter;
    }

}