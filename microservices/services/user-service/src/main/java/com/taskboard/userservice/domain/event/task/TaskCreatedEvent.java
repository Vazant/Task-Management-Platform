package com.taskboard.userservice.domain.event.task;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taskboard.userservice.domain.event.IncomingEvent;

/**
 * Event received when a task is created in the Task Service.
 * 
 * TODO: Add task priority and status fields
 * TODO: Add project information if available
 * TODO: Add due date information
 */
public class TaskCreatedEvent implements IncomingEvent<TaskCreatedEvent.TaskData> {
    
    private final UUID eventId;
    private final String eventType;
    private final String sourceService;
    private final LocalDateTime timestamp;
    private final String version;
    private final TaskData data;
    
    public TaskCreatedEvent(UUID eventId, String eventType, String sourceService, 
                          LocalDateTime timestamp, String version, TaskData data) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.sourceService = sourceService;
        this.timestamp = timestamp;
        this.version = version;
        this.data = data;
    }
    
    @Override
    public UUID getEventId() {
        return eventId;
    }
    
    @Override
    public String getEventType() {
        return eventType;
    }
    
    @Override
    public String getSourceService() {
        return sourceService;
    }
    
    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String getVersion() {
        return version;
    }
    
    @Override
    public TaskData getData() {
        return data;
    }
    
    /**
     * Task data payload for TaskCreatedEvent.
     */
    public static class TaskData {
        private final Long taskId;
        private final String title;
        private final String description;
        private final Long userId;
        private final Long projectId;
        private final String status;
        private final String priority;
        private final LocalDateTime createdAt;
        
        public TaskData(Long taskId, String title, String description, Long userId, 
                       Long projectId, String status, String priority, LocalDateTime createdAt) {
            this.taskId = taskId;
            this.title = title;
            this.description = description;
            this.userId = userId;
            this.projectId = projectId;
            this.status = status;
            this.priority = priority;
            this.createdAt = createdAt;
        }
        
        // Builder pattern for better readability
        public static class Builder {
            private Long taskId;
            private String title;
            private String description;
            private Long userId;
            private Long projectId;
            private String status;
            private String priority;
            private LocalDateTime createdAt;
            
            public Builder taskId(Long taskId) {
                this.taskId = taskId;
                return this;
            }
            
            public Builder title(String title) {
                this.title = title;
                return this;
            }
            
            public Builder description(String description) {
                this.description = description;
                return this;
            }
            
            public Builder userId(Long userId) {
                this.userId = userId;
                return this;
            }
            
            public Builder projectId(Long projectId) {
                this.projectId = projectId;
                return this;
            }
            
            public Builder status(String status) {
                this.status = status;
                return this;
            }
            
            public Builder priority(String priority) {
                this.priority = priority;
                return this;
            }
            
            public Builder createdAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
                return this;
            }
            
            public TaskData build() {
                return new TaskData(taskId, title, description, userId, projectId, status, priority, createdAt);
            }
        }
        
        public static Builder builder() {
            return new Builder();
        }
        
        public Long getTaskId() {
            return taskId;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getDescription() {
            return description;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public Long getProjectId() {
            return projectId;
        }
        
        public String getStatus() {
            return status;
        }
        
        public String getPriority() {
            return priority;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
}
