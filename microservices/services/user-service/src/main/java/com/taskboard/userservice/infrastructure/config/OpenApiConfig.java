package com.taskboard.userservice.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration for OpenAPI/Swagger documentation.
 * 
 * <p>This configuration provides comprehensive API documentation including:
 * <ul>
 *   <li>API information and metadata</li>
 *   <li>Security scheme definitions</li>
 *   <li>Server configurations</li>
 *   <li>Contact and license information</li>
 * </ul>
 * </p>
 * 
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${app.api.version:1.0.0}")
    private String apiVersion;
    
    @Value("${app.api.title:User Service API}")
    private String apiTitle;
    
    @Value("${app.api.description:User Management Service API for Task Management Platform}")
    private String apiDescription;
    
    @Value("${app.api.contact.name:User Service Team}")
    private String contactName;
    
    @Value("${app.api.contact.email:userservice@taskboard.com}")
    private String contactEmail;
    
    @Value("${app.api.contact.url:https://taskboard.com/contact}")
    private String contactUrl;
    
    @Value("${app.api.license.name:MIT License}")
    private String licenseName;
    
    @Value("${app.api.license.url:https://opensource.org/licenses/MIT}")
    private String licenseUrl;
    
    @Value("${app.api.server.url:http://localhost:8080}")
    private String serverUrl;
    
    @Value("${app.api.server.description:Development Server}")
    private String serverDescription;
    
    /**
     * Configures the OpenAPI specification for the User Service API.
     * 
     * <p>This configuration includes:
     * <ul>
     *   <li>API metadata and versioning</li>
     *   <li>JWT Bearer token security scheme</li>
     *   <li>Server configurations for different environments</li>
     *   <li>Contact and license information</li>
     * </ul>
     * </p>
     * 
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(apiInfo())
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("Bearer Authentication", createBearerAuthScheme()))
            .servers(List.of(
                new Server()
                    .url(serverUrl)
                    .description(serverDescription),
                new Server()
                    .url("https://api.taskboard.com")
                    .description("Production Server"),
                new Server()
                    .url("https://staging-api.taskboard.com")
                    .description("Staging Server")
            ));
    }
    
    /**
     * Creates the API information section of the OpenAPI specification.
     * 
     * @return configured Info object
     */
    private Info apiInfo() {
        return new Info()
            .title(apiTitle)
            .description(apiDescription)
            .version(apiVersion)
            .contact(new Contact()
                .name(contactName)
                .email(contactEmail)
                .url(contactUrl))
            .license(new License()
                .name(licenseName)
                .url(licenseUrl))
            .termsOfService("https://taskboard.com/terms");
    }
    
    /**
     * Creates the Bearer token security scheme for JWT authentication.
     * 
     * @return configured SecurityScheme for Bearer authentication
     */
    private SecurityScheme createBearerAuthScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("JWT token obtained from the authentication endpoint. " +
                "Include the token in the Authorization header as 'Bearer {token}'");
    }
}
