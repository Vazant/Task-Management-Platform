package com.taskboard.userservice.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Authentication Entry Point for handling authentication failures.
 * 
 * <p>This class handles cases where a user tries to access a protected resource
 * without proper authentication. It returns a standardized error response
 * with appropriate HTTP status code and error message.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Standardized error response format</li>
 *   <li>Proper HTTP status codes</li>
 *   <li>JSON error response</li>
 *   <li>Timestamp and request information</li>
 * </ul>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private final ObjectMapper objectMapper;
    
    /**
     * Constructs a new JwtAuthenticationEntryPoint.
     */
    public JwtAuthenticationEntryPoint() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Commences an authentication scheme.
     * 
     * <p>This method is called whenever an AuthenticationException is thrown
     * because a user is trying to access a protected resource without
     * proper authentication.</p>
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param authException the authentication exception
     * @throws IOException if I/O error occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "Authentication required to access this resource");
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("details", authException.getMessage());
        
        // Add additional information for debugging (only in development)
        if (isDevelopmentEnvironment()) {
            errorResponse.put("exception", authException.getClass().getSimpleName());
            errorResponse.put("stackTrace", authException.getStackTrace());
        }
        
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
    
    /**
     * Checks if the application is running in development environment.
     * 
     * @return true if in development environment
     */
    private boolean isDevelopmentEnvironment() {
        String activeProfile = System.getProperty("spring.profiles.active");
        return activeProfile == null || activeProfile.contains("dev") || activeProfile.contains("test");
    }
}
