package com.taskboard.userservice.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Access Denied Handler for handling authorization failures.
 * 
 * <p>This class handles cases where a user is authenticated but doesn't have
 * sufficient permissions to access a protected resource. It returns a
 * standardized error response with appropriate HTTP status code and error message.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Standardized error response format</li>
 *   <li>Proper HTTP status codes (403 Forbidden)</li>
 *   <li>JSON error response</li>
 *   <li>Timestamp and request information</li>
 * </ul>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    
    private final ObjectMapper objectMapper;
    
    /**
     * Constructs a new JwtAccessDeniedHandler.
     */
    public JwtAccessDeniedHandler() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Handles an access denied failure.
     * 
     * <p>This method is called whenever an AccessDeniedException is thrown
     * because a user doesn't have sufficient permissions to access a
     * protected resource.</p>
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param accessDeniedException the access denied exception
     * @throws IOException if I/O error occurs
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpServletResponse.SC_FORBIDDEN);
        errorResponse.put("error", "Forbidden");
        errorResponse.put("message", "Access denied. Insufficient permissions to access this resource");
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("details", accessDeniedException.getMessage());
        
        // Add additional information for debugging (only in development)
        if (isDevelopmentEnvironment()) {
            errorResponse.put("exception", accessDeniedException.getClass().getSimpleName());
            errorResponse.put("requiredPermissions", "Check your role and permissions");
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
