package com.taskboard.userservice.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Token Provider for generating and validating JWT tokens.
 * 
 * <p>This component handles JWT token operations including:
 * <ul>
 *   <li>Token generation</li>
 *   <li>Token validation</li>
 *   <li>Token parsing</li>
 *   <li>Claims extraction</li>
 * </ul>
 * 
 * <p>Example usage:
 * 
 * <pre>{@code
 * JwtTokenProvider tokenProvider = new JwtTokenProvider();
 * 
 * // Generate token
 * String token = tokenProvider.generateToken(userDetails);
 * 
 * // Validate token
 * boolean isValid = tokenProvider.validateToken(token, userDetails);
 * 
 * // Extract username
 * String username = tokenProvider.getUsernameFromToken(token);
 * }</pre>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see UserDetails
 * @see Claims
 */
@Component
@Slf4j
public class JwtTokenProvider {

  @Value("${jwt.secret:mySecretKey}")
  private String secret;

  @Value("${jwt.expiration:86400}")
  private Long expiration;

  /**
   * Generates a JWT token for the given user details.
   * 
   * @param userDetails the user details
   * @return the JWT token
   */
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userDetails.getUsername());
  }

  /**
   * Generates a JWT token for the given authentication.
   * 
   * @param authentication the authentication
   * @return the JWT token
   */
  public String generateToken(Authentication authentication) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, authentication.getName());
  }

  /**
   * Generates a JWT token with custom claims.
   * 
   * @param claims the custom claims
   * @param subject the subject (username)
   * @return the JWT token
   */
  public String generateToken(Map<String, Object> claims, String subject) {
    return createToken(claims, subject);
  }

  /**
   * Creates a JWT token with the given claims and subject.
   * 
   * @param claims the claims
   * @param subject the subject
   * @return the JWT token
   */
  private String createToken(Map<String, Object> claims, String subject) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration * 1000);

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  /**
   * Extracts the username from the JWT token.
   * 
   * @param token the JWT token
   * @return the username
   */
  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  /**
   * Extracts the expiration date from the JWT token.
   * 
   * @param token the JWT token
   * @return the expiration date
   */
  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  /**
   * Extracts a specific claim from the JWT token.
   * 
   * @param token the JWT token
   * @param claimsResolver the claims resolver function
   * @param <T> the type of the claim
   * @return the claim value
   */
  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Extracts all claims from the JWT token.
   * 
   * @param token the JWT token
   * @return all claims
   */
  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * Checks if the JWT token is expired.
   * 
   * @param token the JWT token
   * @return true if the token is expired
   */
  public Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  /**
   * Validates the JWT token against the user details.
   * 
   * @param token the JWT token
   * @param userDetails the user details
   * @return true if the token is valid
   */
  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  /**
   * Validates the JWT token.
   * 
   * @param token the JWT token
   * @return true if the token is valid
   */
  public Boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      log.warn("Invalid JWT token: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Gets the signing key for JWT operations.
   * 
   * @return the signing key
   */
  private SecretKey getSigningKey() {
    byte[] keyBytes = secret.getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Gets the token expiration time in seconds.
   * 
   * @return the expiration time in seconds
   */
  public Long getExpiration() {
    return expiration;
  }

  /**
   * Sets the token expiration time in seconds.
   * 
   * @param expiration the expiration time in seconds
   */
  public void setExpiration(Long expiration) {
    this.expiration = expiration;
  }

  /**
   * Gets the token expiration time in seconds (alias for getExpiration).
   * 
   * @return the expiration time in seconds
   */
  public Long getExpirationTime() {
    return expiration;
  }
}
