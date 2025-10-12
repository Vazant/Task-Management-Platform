package com.taskboard.userservice.infrastructure.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * Web layer configuration for the application.
 * Configures CORS, interceptors, and other web-related components.
 * 
 * <p>This configuration class handles:
 * <ul>
 *   <li>Cross-Origin Resource Sharing (CORS) settings</li>
 *   <li>Web interceptors for request/response processing</li>
 *   <li>Static resource handling</li>
 *   <li>Content negotiation settings</li>
 * </ul>
 * 
 * <p>CORS configuration allows controlled access from specified origins
 * with appropriate headers and methods for API consumption.
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.security.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${app.security.cors.allowed-methods:*}")
    private String allowedMethods;

    @Value("${app.security.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${app.security.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${app.security.cors.max-age:3600}")
    private long maxAge;

    /**
     * Configures CORS settings for API endpoints.
     * 
     * <p>This method sets up Cross-Origin Resource Sharing to allow
     * controlled access from specified origins with appropriate
     * headers and HTTP methods.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(parseAllowedOrigins())
                .allowedMethods(parseAllowedMethods())
                .allowedHeaders(parseAllowedHeaders())
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }

    /**
     * Creates a CORS configuration source bean for Spring Security.
     * 
     * @return CorsConfigurationSource for programmatic CORS configuration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOriginPatterns(parseAllowedOrigins());
        configuration.setAllowedMethods(parseAllowedMethods());
        configuration.setAllowedHeaders(parseAllowedHeaders());
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);
        
        // Добавляем заголовки, которые могут быть отправлены клиентом
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Content-Type");
        configuration.addExposedHeader("X-Requested-With");
        configuration.addExposedHeader("Accept");
        configuration.addExposedHeader("Origin");
        configuration.addExposedHeader("Access-Control-Request-Method");
        configuration.addExposedHeader("Access-Control-Request-Headers");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }

    /**
     * Парсит разрешенные origins из конфигурации.
     */
    private List<String> parseAllowedOrigins() {
        if ("*".equals(allowedOrigins)) {
            return List.of("*");
        }
        return Arrays.asList(allowedOrigins.split(","));
    }

    /**
     * Парсит разрешенные HTTP методы из конфигурации.
     */
    private List<String> parseAllowedMethods() {
        if ("*".equals(allowedMethods)) {
            return List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");
        }
        return Arrays.asList(allowedMethods.split(","));
    }

    /**
     * Парсит разрешенные заголовки из конфигурации.
     */
    private List<String> parseAllowedHeaders() {
        if ("*".equals(allowedHeaders)) {
            return List.of("*");
        }
        return Arrays.asList(allowedHeaders.split(","));
    }
}
