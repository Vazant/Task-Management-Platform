package com.taskboard.userservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatistics {
    
    private Long id;
    private Long userId;
    private int totalTasks;
    private int completedTasks;
    private int inProgressTasks;
    private int todoTasks;
    private int activeProjects;
    private int totalProjects;
    private LocalDateTime lastActivity;
    private LocalDateTime lastTaskCreated;
    private LocalDateTime lastTaskCompleted;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Lombok @Data annotation provides getters and setters automatically
    
    // Lombok @Data annotation provides all getters automatically
    
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
    
    // Lombok @Builder annotation provides builder pattern automatically
    
    /**
     * Gets pending tasks count (TODO + IN_PROGRESS).
     * 
     * @return the pending task count
     */
    public int getPendingTasks() {
        return todoTasks + inProgressTasks;
    }
    
    /**
     * Gets overdue tasks count (placeholder for future implementation).
     * 
     * @return the overdue task count
     */
    public int getOverdueTasks() {
        // TODO: Implement overdue task calculation based on due dates
        return 0;
    }
    
    /**
     * Creates a copy of this statistics with updated values.
     * 
     * @return a new builder initialized with current values
     */
    public UserStatisticsBuilder toBuilder() {
        return UserStatistics.builder()
            .id(id)
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
            .lastLoginAt(lastLoginAt)
            .createdAt(createdAt)
            .updatedAt(updatedAt);
    }
}
