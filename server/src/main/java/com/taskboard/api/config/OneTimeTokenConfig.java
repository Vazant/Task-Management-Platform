package com.taskboard.api.config;

import com.taskboard.api.service.OneTimeTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * Configuration for One-Time Token Login using Spring Security 6.5 DSL
 * Provides secure temporary access without requiring passwords
 */
@Configuration
public class OneTimeTokenConfig {

    @Autowired
    private OneTimeTokenService oneTimeTokenService;

    /**
     * Success handler for One-Time Token authentication
     */
    @Bean
    public AuthenticationSuccessHandler oneTimeTokenSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/api/auth/ott-success");
        handler.setAlwaysUseDefaultTargetUrl(true);
        return handler;
    }

    /**
     * Failure handler for One-Time Token authentication
     */
    @Bean
    public AuthenticationFailureHandler oneTimeTokenFailureHandler() {
        SimpleUrlAuthenticationFailureHandler handler = new SimpleUrlAuthenticationFailureHandler();
        handler.setDefaultFailureUrl("/api/auth/ott-failure");
        return handler;
    }

    /**
     * Custom handler for One-Time Token generation success
     * This handles the delivery of tokens (email, SMS, etc.)
     */
    @Bean
    public OneTimeTokenGenerationSuccessHandler oneTimeTokenGenerationSuccessHandler() {
        return new OneTimeTokenGenerationSuccessHandler();
    }

    /**
     * Custom handler for One-Time Token generation success
     * Handles token delivery to users
     */
    public static class OneTimeTokenGenerationSuccessHandler implements AuthenticationSuccessHandler {
        
        @Override
        public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request, 
                                          jakarta.servlet.http.HttpServletResponse response, 
                                          org.springframework.security.core.Authentication authentication) {
            // This would typically send the token via email, SMS, or other delivery method
            // For now, we'll just log the success
            System.out.println("One-Time Token generated successfully for user: " + authentication.getName());
        }
    }
}
