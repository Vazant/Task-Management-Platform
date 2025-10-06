package com.taskboard.api.config;

import com.taskboard.api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** JWT аутентификационный фильтр для проверки токенов в запросах. */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired private JwtService jwtService;

  @Autowired private UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;

    log.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      log.debug("No Authorization header or invalid format for: {}", request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }

    jwt = authHeader.substring(7);

    try {
      username = jwtService.extractUsername(jwt);
      log.debug("Extracted username: {} from JWT", username);
    } catch (Exception e) {
      log.warn("Failed to extract username from JWT: {}", e.getMessage());
      filterChain.doFilter(request, response);
      return;
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

      if (jwtService.validateToken(jwt, userDetails)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.debug(
            "Authentication set for user: {} with authorities: {}",
            username,
            userDetails.getAuthorities());
      } else {
        log.warn("JWT validation failed for user: {}", username);
      }
    }

    filterChain.doFilter(request, response);
  }
}
