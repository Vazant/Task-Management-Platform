package com.taskboard.common.util;

import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for service URLs and external services
 * Uses environment variables for flexible configuration across different environments
 */
@Configuration
@ConfigurationProperties(prefix = "service")
@Data
public class ServiceConfig {
    
    private Urls urls = new Urls();
    private External external = new External();
    
    @Value
    public static class Urls {
        String userService;
        String taskService;
        String projectService;
        String timeService;
        String analyticsService;
        String notificationService;
        String searchService;
        String fileService;
        String gatewayService;
    }
    
    @Value
    public static class External {
        String prometheus;
        String jaeger;
        String kafkaUi;
        String grafana;
    }
}

