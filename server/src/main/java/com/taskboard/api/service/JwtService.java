package com.taskboard.api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("checkstyle:DesignForExtension")
public class JwtService {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private Long expiration;

  @Value("${jwt.refresh-expiration}")
  private Long refreshExpiration;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String extractUsername(final String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(final String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(final String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  private Boolean isTokenExpired(final String token) {
    return extractExpiration(token).before(new Date());
  }

  public String generateToken(final UserDetails userDetails) {
    final Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userDetails.getUsername(), expiration);
  }

  public String generateRefreshToken(final UserDetails userDetails) {
    final Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userDetails.getUsername(), refreshExpiration);
  }

  private String createToken(final Map<String, Object> claims, final String subject, final long expiration) {
    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey())
        .compact();
  }

  public Boolean validateToken(final String token, final UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  public Boolean validateToken(final String token, final String username) {
    final String tokenUsername = extractUsername(token);
    return tokenUsername.equals(username) && !isTokenExpired(token);
  }

  public String generateToken(final String username) {
    final Map<String, Object> claims = new HashMap<>();
    return createToken(claims, username, expiration);
  }

  public String generateRefreshToken(final String username) {
    final Map<String, Object> claims = new HashMap<>();
    return createToken(claims, username, refreshExpiration);
  }
}
