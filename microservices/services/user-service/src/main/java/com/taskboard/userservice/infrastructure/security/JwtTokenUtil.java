package com.taskboard.userservice.infrastructure.security;

import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT token operations.
 * 
 * <p>This class provides methods for generating, validating, and extracting
 * information from JWT tokens. It handles token creation, expiration,
 * and claims extraction.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Token generation with custom claims</li>
 *   <li>Token validation and expiration checking</li>
 *   <li>Username and claims extraction</li>
 *   <li>Refresh token support</li>
 * </ul>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class JwtTokenUtil {
    
    private static final String USER_ID_CLAIM = "userId";
    
    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;
    
    @Value("${spring.security.jwt.expiration}")
    private Long jwtExpiration;
    
    @Value("${spring.security.jwt.refresh-expiration}")
    private Long jwtRefreshExpiration;
    
    /**
     * Generates a JWT token for the given user.
     * 
     * @param user the user to generate token for
     * @return JWT token string
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID_CLAIM, user.getId());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        claims.put("status", user.getStatus().name());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("emailVerified", user.isEmailVerified());
        
        return createToken(claims, user.getUsername(), jwtExpiration);
    }
    
    /**
     * Generates a refresh token for the given user.
     * 
     * @param user the user to generate refresh token for
     * @return JWT refresh token string
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID_CLAIM, user.getId());
        claims.put("username", user.getUsername());
        claims.put("type", "refresh");
        
        return createToken(claims, user.getUsername(), jwtRefreshExpiration);
    }
    
    /**
     * Creates a JWT token with the specified claims and expiration.
     * 
     * @param claims the claims to include in the token
     * @param subject the subject (usually username)
     * @param expiration the expiration time in seconds
     * @return JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Extracts the username from a JWT token.
     * 
     * @param token the JWT token
     * @return username from the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extracts the expiration date from a JWT token.
     * 
     * @param token the JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extracts the user ID from a JWT token.
     * 
     * @param token the JWT token
     * @return user ID from the token
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get(USER_ID_CLAIM, Long.class));
    }
    
    /**
     * Extracts the user role from a JWT token.
     * 
     * @param token the JWT token
     * @return user role from the token
     */
    public UserRole extractUserRole(String token) {
        String roleString = extractClaim(token, claims -> claims.get("role", String.class));
        return roleString != null ? UserRole.valueOf(roleString) : null;
    }
    
    /**
     * Extracts a specific claim from a JWT token.
     * 
     * @param token the JWT token
     * @param claimsResolver function to extract the claim
     * @param <T> the type of the claim
     * @return the extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extracts all claims from a JWT token.
     * 
     * @param token the JWT token
     * @return all claims from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Checks if a JWT token is expired.
     * 
     * @param token the JWT token
     * @return true if the token is expired
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Validates a JWT token against user details.
     * 
     * @param token the JWT token
     * @param userDetails the user details to validate against
     * @return true if the token is valid
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    /**
     * Validates a JWT token without user details.
     * 
     * @param token the JWT token
     * @return true if the token is valid
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if a token is a refresh token.
     * 
     * @param token the JWT token
     * @return true if the token is a refresh token
     */
    public Boolean isRefreshToken(String token) {
        String type = extractClaim(token, claims -> claims.get("type", String.class));
        return "refresh".equals(type);
    }
    
    /**
     * Gets the signing key for JWT operations.
     * 
     * @return the signing key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
