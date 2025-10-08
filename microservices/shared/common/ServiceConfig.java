package com.taskboard.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration class for service URLs and external services
 * Uses environment variables for flexible configuration across different environments
 */
@Configuration
@ConfigurationProperties(prefix = "service")
public class ServiceConfig {
    
    private Urls urls = new Urls();
    private External external = new External();
    
    public Urls getUrls() {
        return urls;
    }
    
    public void setUrls(Urls urls) {
        this.urls = urls;
    }
    
    public External getExternal() {
        return external;
    }
    
    public void setExternal(External external) {
        this.external = external;
    }
    
    public static class Urls {
        private String userService;
        private String taskService;
        private String projectService;
        private String timeService;
        private String analyticsService;
        private String notificationService;
        private String searchService;
        private String fileService;
        private String gatewayService;
        
        // Getters and setters
        public String getUserService() {
            return userService;
        }
        
        public void setUserService(String userService) {
            this.userService = userService;
        }
        
        public String getTaskService() {
            return taskService;
        }
        
        public void setTaskService(String taskService) {
            this.taskService = taskService;
        }
        
        public String getProjectService() {
            return projectService;
        }
        
        public void setProjectService(String projectService) {
            this.projectService = projectService;
        }
        
        public String getTimeService() {
            return timeService;
        }
        
        public void setTimeService(String timeService) {
            this.timeService = timeService;
        }
        
        public String getAnalyticsService() {
            return analyticsService;
        }
        
        public void setAnalyticsService(String analyticsService) {
            this.analyticsService = analyticsService;
        }
        
        public String getNotificationService() {
            return notificationService;
        }
        
        public void setNotificationService(String notificationService) {
            this.notificationService = notificationService;
        }
        
        public String getSearchService() {
            return searchService;
        }
        
        public void setSearchService(String searchService) {
            this.searchService = searchService;
        }
        
        public String getFileService() {
            return fileService;
        }
        
        public void setFileService(String fileService) {
            this.fileService = fileService;
        }
        
        public String getGatewayService() {
            return gatewayService;
        }
        
        public void setGatewayService(String gatewayService) {
            this.gatewayService = gatewayService;
        }
    }
    
    public static class External {
        private String prometheus;
        private String jaeger;
        private String kafkaUi;
        private String grafana;
        
        // Getters and setters
        public String getPrometheus() {
            return prometheus;
        }
        
        public void setPrometheus(String prometheus) {
            this.prometheus = prometheus;
        }
        
        public String getJaeger() {
            return jaeger;
        }
        
        public void setJaeger(String jaeger) {
            this.jaeger = jaeger;
        }
        
        public String getKafkaUi() {
            return kafkaUi;
        }
        
        public void setKafkaUi(String kafkaUi) {
            this.kafkaUi = kafkaUi;
        }
        
        public String getGrafana() {
            return grafana;
        }
        
        public void setGrafana(String grafana) {
            this.grafana = grafana;
        }
    }
}
