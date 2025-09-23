package com.taskboard.api.config;

import com.taskboard.api.service.DpopTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter for processing DPoP (Demonstrating Proof of Possession) tokens
 * Validates DPoP proof tokens and ensures they match the access token
 */
@Component
@Slf4j
public class DpopAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private DpopTokenService dpopTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String dpopHeader = request.getHeader("DPoP");
        String authorizationHeader = request.getHeader("Authorization");
        
        // Only process if both DPoP and Authorization headers are present
        if (dpopHeader != null && authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String accessToken = authorizationHeader.substring(7); // Remove "Bearer " prefix
                String httpMethod = request.getMethod();
                String httpUrl = request.getRequestURL().toString();
                
                log.debug("Processing DPoP token for {} {}", httpMethod, httpUrl);
                
                // Validate DPoP proof token
                boolean isValid = dpopTokenService.validateDpopProof(
                    dpopHeader, 
                    accessToken, 
                    httpMethod, 
                    httpUrl, 
                    null // nonce not used in this implementation
                );
                
                if (!isValid) {
                    log.warn("Invalid DPoP proof token for {} {}", httpMethod, httpUrl);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"invalid_dpop_proof\"}");
                    return;
                }
                
                log.debug("DPoP proof token validated successfully");
                
            } catch (Exception e) {
                log.error("Error processing DPoP token: {}", e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"dpop_processing_error\"}");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip DPoP validation for public endpoints
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") || 
               path.startsWith("/api/test/public") || 
               path.startsWith("/h2-console/") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs");
    }
}