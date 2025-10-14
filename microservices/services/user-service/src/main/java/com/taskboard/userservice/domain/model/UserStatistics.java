package com.taskboard.userservice.domain.model;

import java.time.LocalDateTime;

/**
 * Domain entity representing user statistics and activity tracking.
 * 
 * <p>This entity aggregates various statistics about user activity,
 * including task-related metrics, project participation, and activity patterns.</p>
 * 
 * <p>The entity follows the domain-driven design principles and encapsulates
 * business logic related to user statistics calculation and updates.</p>
 * 
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
public class UserStatistics {
    
    private final Long userId;
    private final int totalTasks;
    private final int completedTasks;
    private final int inProgressTasks;
    private final int todoTasks;
    private final int activeProjects;
    private final int totalProjects;
    private final LocalDateTime lastActivity;
    private final LocalDateTime lastTaskCreated;
    private final LocalDateTime lastTaskCompleted;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    /**
     * Private constructor to enforce use of builder pattern.
     */
    private UserStatistics(Builder builder) {
        this.userId = builder.userId;
        this.totalTasks = builder.totalTasks;
        this.completedTasks = builder.completedTasks;
        this.inProgressTasks = builder.inProgressTasks;
        this.todoTasks = builder.todoTasks;
        this.activeProjects = builder.activeProjects;
        this.totalProjects = builder.totalProjects;
        this.lastActivity = builder.lastActivity;
        this.lastTaskCreated = builder.lastTaskCreated;
        this.lastTaskCompleted = builder.lastTaskCompleted;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }
    
    /**
     * Gets the user ID.
     * 
     * @return the user ID
     */
    public Long getUserId() {
        return userId;
    }
    
    /**
     * Gets the total number of tasks.
     * 
     * @return the total task count
     */
    public int getTotalTasks() {
        return totalTasks;
    }
    
    /**
     * Gets the number of completed tasks.
     * 
     * @return the completed task count
     */
    public int getCompletedTasks() {
        return completedTasks;
    }
    
    /**
     * Gets the number of in-progress tasks.
     * 
     * @return the in-progress task count
     */
    public int getInProgressTasks() {
        return inProgressTasks;
    }
    
    /**
     * Gets the number of TODO tasks.
     * 
     * @return the TODO task count
     */
    public int getTodoTasks() {
        return todoTasks;
    }
    
    /**
     * Gets the number of active projects.
     * 
     * @return the active project count
     */
    public int getActiveProjects() {
        return activeProjects;
    }
    
    /**
     * Gets the total number of projects.
     * 
     * @return the total project count
     */
    public int getTotalProjects() {
        return totalProjects;
    }
    
    /**
     * Gets the last activity timestamp.
     * 
     * @return the last activity time
     */
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    
    /**
     * Gets the last task creation timestamp.
     * 
     * @return the last task creation time
     */
    public LocalDateTime getLastTaskCreated() {
        return lastTaskCreated;
    }
    
    /**
     * Gets the last task completion timestamp.
     * 
     * @return the last task completion time
     */
    public LocalDateTime getLastTaskCompleted() {
        return lastTaskCompleted;
    }
    
    /**
     * Gets the creation timestamp.
     * 
     * @return the creation time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Gets the last update timestamp.
     * 
     * @return the last update time
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Calculates the task completion rate.
     * 
     * @return the completion rate as a percentage (0.0 to 100.0)
     */
    public double getTaskCompletionRate() {
        if (totalTasks == 0) {
            return 0.0;
        }
        return (double) completedTasks / totalTasks * 100.0;
    }
    
    /**
     * Calculates the project participation rate.
     * 
     * @return the participation rate as a percentage (0.0 to 100.0)
     */
    public double getProjectParticipationRate() {
        if (totalProjects == 0) {
            return 0.0;
        }
        return (double) activeProjects / totalProjects * 100.0;
    }
    
    /**
     * Checks if the user is active based on recent activity.
     * 
     * @return true if user has been active in the last 7 days
     */
    public boolean isActive() {
        if (lastActivity == null) {
            return false;
        }
        return lastActivity.isAfter(LocalDateTime.now().minusDays(7));
    }
    
    /**
     * Gets the days since last activity.
     * 
     * @return the number of days since last activity, or -1 if never active
     */
    public long getDaysSinceLastActivity() {
        if (lastActivity == null) {
            return -1;
        }
        return java.time.Duration.between(lastActivity, LocalDateTime.now()).toDays();
    }
    
    /**
     * Builder pattern for creating UserStatistics instances.
     */
    public static class Builder {
        private Long userId;
        private int totalTasks = 0;
        private int completedTasks = 0;
        private int inProgressTasks = 0;
        private int todoTasks = 0;
        private int activeProjects = 0;
        private int totalProjects = 0;
        private LocalDateTime lastActivity;
        private LocalDateTime lastTaskCreated;
        private LocalDateTime lastTaskCompleted;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();
        
        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder totalTasks(int totalTasks) {
            this.totalTasks = totalTasks;
            return this;
        }
        
        public Builder completedTasks(int completedTasks) {
            this.completedTasks = completedTasks;
            return this;
        }
        
        public Builder inProgressTasks(int inProgressTasks) {
            this.inProgressTasks = inProgressTasks;
            return this;
        }
        
        public Builder todoTasks(int todoTasks) {
            this.todoTasks = todoTasks;
            return this;
        }
        
        public Builder activeProjects(int activeProjects) {
            this.activeProjects = activeProjects;
            return this;
        }
        
        public Builder totalProjects(int totalProjects) {
            this.totalProjects = totalProjects;
            return this;
        }
        
        public Builder lastActivity(LocalDateTime lastActivity) {
            this.lastActivity = lastActivity;
            return this;
        }
        
        public Builder lastTaskCreated(LocalDateTime lastTaskCreated) {
            this.lastTaskCreated = lastTaskCreated;
            return this;
        }
        
        public Builder lastTaskCompleted(LocalDateTime lastTaskCompleted) {
            this.lastTaskCompleted = lastTaskCompleted;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public UserStatistics build() {
            if (userId == null) {
                throw new IllegalArgumentException("User ID is required");
            }
            
            // Validate that task counts are consistent
            if (totalTasks != completedTasks + inProgressTasks + todoTasks) {
                throw new IllegalArgumentException(
                    "Total tasks must equal sum of completed, in-progress, and TODO tasks");
            }
            
            // Validate that project counts are consistent
            if (activeProjects > totalProjects) {
                throw new IllegalArgumentException(
                    "Active projects cannot exceed total projects");
            }
            
            return new UserStatistics(this);
        }
    }
    
    /**
     * Creates a new builder instance.
     * 
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Creates a copy of this statistics with updated values.
     * 
     * @return a new builder initialized with current values
     */
    public Builder toBuilder() {
        return new Builder()
            .userId(userId)
            .totalTasks(totalTasks)
            .completedTasks(completedTasks)
            .inProgressTasks(inProgressTasks)
            .todoTasks(todoTasks)
            .activeProjects(activeProjects)
            .totalProjects(totalProjects)
            .lastActivity(lastActivity)
            .lastTaskCreated(lastTaskCreated)
            .lastTaskCompleted(lastTaskCompleted)
            .createdAt(createdAt)
            .updatedAt(updatedAt);
    }
}
