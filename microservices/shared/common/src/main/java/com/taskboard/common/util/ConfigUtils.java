package com.taskboard.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for configuration management
 * Provides methods to get configuration values with fallbacks
 */
@Component
public class ConfigUtils {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${management.endpoints.web.base-path:/actuator}")
    private String actuatorBasePath;
    
    @Value("${spring.application.name:microservice}")
    private String serviceName;
    
    /**
     * Get the health check URL for the current service
     * @return Health check URL
     */
    public String getHealthCheckUrl() {
        return String.format("http://localhost:%s%s/health", serverPort, actuatorBasePath);
    }
    
    /**
     * Get the actuator base URL for the current service
     * @return Actuator base URL
     */
    public String getActuatorBaseUrl() {
        return String.format("http://localhost:%s%s", serverPort, actuatorBasePath);
    }
    
    /**
     * Get the service name
     * @return Service name
     */
    public String getServiceName() {
        return serviceName;
    }
    
    /**
     * Get the server port
     * @return Server port
     */
    public String getServerPort() {
        return serverPort;
    }
    
    /**
     * Get the actuator base path
     * @return Actuator base path
     */
    public String getActuatorBasePath() {
        return actuatorBasePath;
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

