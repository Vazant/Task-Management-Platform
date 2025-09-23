package com.taskboard.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for working with DPoP (Demonstrating Proof of Possession) tokens
 * Provides additional security for OAuth 2.0 tokens by binding them to client keys
 * 
 * DPoP ensures that only the client that requested an access token can use it,
 * preventing token theft and replay attacks.
 */
@Service
@Slf4j
public class DpopTokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates a DPoP proof token for HTTP request
     * 
     * @param accessToken the original access token
     * @param httpMethod HTTP method (GET, POST, etc.)
     * @param httpUrl request URL
     * @param nonce unique nonce to prevent replay attacks
     * @return DPoP proof token
     */
    public String createDpopProof(String accessToken, String httpMethod, String httpUrl, String nonce) {
        try {
            // Create hash from access token
            String ath = createAccessTokenHash(accessToken);
            
            // Create claims for DPoP proof
            Map<String, Object> claims = new HashMap<>();
            claims.put("jti", generateJti());
            claims.put("iat", Instant.now().getEpochSecond());
            claims.put("exp", Instant.now().plusSeconds(expiration / 1000).getEpochSecond());
            claims.put("htm", httpMethod);
            claims.put("htu", httpUrl);
            claims.put("ath", ath);
            
            if (nonce != null) {
                claims.put("nonce", nonce);
            }

            // Create DPoP proof token
            return Jwts.builder()
                    .claims(claims)
                    .signWith(getSigningKey())
                    .compact();
                    
        } catch (Exception e) {
            log.error("Error creating DPoP proof token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create DPoP proof token", e);
        }
    }

    /**
     * Validates DPoP proof token
     * 
     * @param dpopProof DPoP proof token
     * @param accessToken original access token
     * @param httpMethod HTTP method
     * @param httpUrl request URL
     * @param nonce expected nonce
     * @return true if token is valid
     */
    public boolean validateDpopProof(String dpopProof, String accessToken, String httpMethod, String httpUrl, String nonce) {
        try {
            // Parse and validate JWT
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(dpopProof)
                    .getPayload();

            // Validate required claims
            if (!validateRequiredClaims(claims, httpMethod, httpUrl, nonce)) {
                return false;
            }

            // Validate access token hash
            String expectedAth = createAccessTokenHash(accessToken);
            String actualAth = claims.get("ath", String.class);
            if (!expectedAth.equals(actualAth)) {
                log.warn("Access token hash mismatch in DPoP proof");
                return false;
            }

            // Validate timestamp
            if (isTokenExpired(claims)) {
                log.warn("DPoP proof token has expired");
                return false;
            }

            log.debug("DPoP proof token validated successfully");
            return true;

        } catch (Exception e) {
            log.error("Error validating DPoP proof token: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Validates required claims in DPoP proof
     */
    private boolean validateRequiredClaims(Claims claims, String httpMethod, String httpUrl, String nonce) {
        // Validate HTTP method
        String htm = claims.get("htm", String.class);
        if (!httpMethod.equals(htm)) {
            log.warn("HTTP method mismatch in DPoP proof: expected {}, got {}", httpMethod, htm);
            return false;
        }

        // Validate HTTP URL
        String htu = claims.get("htu", String.class);
        if (!httpUrl.equals(htu)) {
            log.warn("HTTP URL mismatch in DPoP proof: expected {}, got {}", httpUrl, htu);
            return false;
        }

        // Validate nonce if provided
        if (nonce != null) {
            String proofNonce = claims.get("nonce", String.class);
            if (!nonce.equals(proofNonce)) {
                log.warn("Nonce mismatch in DPoP proof: expected {}, got {}", nonce, proofNonce);
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if token has expired
     */
    private boolean isTokenExpired(Claims claims) {
        Long exp = claims.get("exp", Long.class);
        if (exp == null) {
            return true; // No expiration claim means invalid
        }
        return Instant.now().getEpochSecond() > exp;
    }

    /**
     * Creates SHA-256 hash of access token for ath claim
     */
    private String createAccessTokenHash(String accessToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(accessToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            log.error("Error creating access token hash: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create access token hash", e);
        }
    }

    /**
     * Generates unique JTI (JWT ID)
     */
    private String generateJti() {
        return UUID.randomUUID().toString();
    }

    /**
     * Gets signing key for JWT
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}