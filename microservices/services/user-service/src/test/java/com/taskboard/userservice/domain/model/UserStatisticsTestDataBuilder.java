package com.taskboard.userservice.domain.model;

import java.time.LocalDateTime;

/**
 * Test data builder for UserStatistics domain model.
 * Provides fluent API for creating test user statistics with different configurations.
 */
public class UserStatisticsTestDataBuilder {
    
    private Long id = 1L;
    private Long userId = 1L;
    private int totalTasks = 0;
    private int completedTasks = 0;
    private int activeProjects = 0;
    private int completedProjects = 0;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static UserStatisticsTestDataBuilder aUserStatistics() {
        return new UserStatisticsTestDataBuilder();
    }
    
    public UserStatisticsTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }
    
    public UserStatisticsTestDataBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }
    
    public UserStatisticsTestDataBuilder withTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
        return this;
    }
    
    public UserStatisticsTestDataBuilder withCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
        return this;
    }
    
    public UserStatisticsTestDataBuilder withActiveProjects(int activeProjects) {
        this.activeProjects = activeProjects;
        return this;
    }
    
    public UserStatisticsTestDataBuilder withCompletedProjects(int completedProjects) {
        this.completedProjects = completedProjects;
        return this;
    }
    
    public UserStatisticsTestDataBuilder withLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
        return this;
    }
    
    public UserStatisticsTestDataBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
    
    public UserStatisticsTestDataBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
    
    public UserStatistics build() {
        UserStatistics stats = UserStatistics.builder()
            .id(id)
            .userId(userId)
            .totalTasks(totalTasks)
            .completedTasks(completedTasks)
            .activeProjects(activeProjects)
            .totalProjects(completedProjects)
            .lastActivity(lastLoginAt)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
        
        // Set timestamps if not provided
        if (stats.getCreatedAt() == null) {
            stats.setCreatedNow();
        }
        if (stats.getUpdatedAt() == null) {
            stats.setUpdatedNow();
        }
        
        return stats;
    }
}
