package com.taskboard.userservice.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Request Filter for processing JWT tokens in HTTP requests.
 * 
 * <p>This filter intercepts HTTP requests and processes JWT tokens for authentication.
 * It extracts the token from the Authorization header, validates it, and sets up
 * the security context for the authenticated user.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Token extraction from Authorization header</li>
 *   <li>Token validation and user authentication</li>
 *   <li>Security context setup</li>
 *   <li>Request chain continuation</li>
 * </ul>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    
    /**
     * Constructs a new JwtRequestFilter with required dependencies.
     * 
     * @param userDetailsService service for loading user details
     * @param jwtTokenUtil utility for JWT token operations
     */
    public JwtRequestFilter(UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }
    
    /**
     * Processes the HTTP request to extract and validate JWT token.
     * 
     * <p>This method:</p>
     * <ol>
     *   <li>Extracts the JWT token from the Authorization header</li>
     *   <li>Validates the token and extracts username</li>
     *   <li>Loads user details and creates authentication</li>
     *   <li>Sets the security context</li>
     *   <li>Continues the filter chain</li>
     * </ol>
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String requestTokenHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwtToken = null;
        
        // Extract token from Authorization header
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.extractUsername(jwtToken);
            } catch (Exception e) {
                logger.warn("Unable to get JWT Token or JWT Token has expired: " + e.getMessage());
            }
        } else {
            logger.debug("JWT Token does not begin with Bearer String");
        }
        
        // Validate token and set up authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Validate token against user details
                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    logger.debug("Successfully authenticated user: " + username);
                } else {
                    logger.warn("JWT Token validation failed for user: " + username);
                }
            } catch (Exception e) {
                logger.error("Error loading user details for username: " + username, e);
            }
        }
        
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
    
    /**
     * Determines if the filter should be applied to the request.
     * 
     * <p>This method can be overridden to skip the filter for certain requests,
     * such as public endpoints or health checks.</p>
     * 
     * @param request the HTTP request
     * @return true if the filter should be applied
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip filter for public endpoints
        return path.startsWith("/api/auth/") ||
               path.startsWith("/actuator/health") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/api-docs/") ||
               path.startsWith("/v3/api-docs/");
    }
}
