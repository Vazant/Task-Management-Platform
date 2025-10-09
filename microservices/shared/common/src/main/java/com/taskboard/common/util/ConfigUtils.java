package com.taskboard.common.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for configuration management
 * Provides methods to get configuration values with fallbacks
 */
@Component
@Getter
public class ConfigUtils {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${management.endpoints.web.base-path:/actuator}")
    private String actuatorBasePath;
    
    @Value("${spring.application.name:microservice}")
    private String serviceName;
    
    @Value("${server.host:localhost}")
    private String serverHost;
    
    @Value("${management.endpoints.web.exposure.include:health,info}")
    private String exposedEndpoints;
    
    /**
     * Get the health check URL for the current service
     * @return Health check URL
     */
    public String getHealthCheckUrl() {
        return String.format("http://%s:%s%s/health", serverHost, serverPort, actuatorBasePath);
    }
    
    /**
     * Get the health check URL for the current service with custom host
     * @param host Custom host name or IP
     * @return Health check URL
     */
    public String getHealthCheckUrl(String host) {
        return String.format("http://%s:%s%s/health", host, serverPort, actuatorBasePath);
    }
    
    /**
     * Get the actuator base URL for the current service
     * @return Actuator base URL
     */
    public String getActuatorBaseUrl() {
        return String.format("http://%s:%s%s", serverHost, serverPort, actuatorBasePath);
    }
    
    
    /**
     * Build service URL with given host and port
     * @param host Host name or IP
     * @param port Port number
     * @return Service URL
     */
    public String buildServiceUrl(String host, String port) {
        return String.format("http://%s:%s", host, port);
    }
    
    /**
     * Build service URL with given host and default port
     * @param host Host name or IP
     * @return Service URL
     */
    public String buildServiceUrl(String host) {
        return buildServiceUrl(host, serverPort);
    }
}

