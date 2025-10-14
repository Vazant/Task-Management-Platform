package com.taskboard.userservice.domain.event.task;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taskboard.userservice.domain.event.IncomingEvent;

/**
 * Event received when a task is updated in the Task Service.
 * 
 * TODO: Add change tracking (what fields were changed)
 * TODO: Add previous values for audit purposes
 * TODO: Add update reason/comment
 */
public class TaskUpdatedEvent implements IncomingEvent<TaskUpdatedEvent.TaskData> {
    
    private final UUID eventId;
    private final String eventType;
    private final String sourceService;
    private final LocalDateTime timestamp;
    private final String version;
    private final TaskData data;
    
    public TaskUpdatedEvent(UUID eventId, String eventType, String sourceService, 
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
     * Task data payload for TaskUpdatedEvent.
     */
    public static class TaskData {
        private final Long taskId;
        private final String title;
        private final String description;
        private final Long userId;
        private final Long projectId;
        private final String status;
        private final String priority;
        private final LocalDateTime updatedAt;
        private final String updatedBy;
        
        public TaskData(Long taskId, String title, String description, Long userId, 
                       Long projectId, String status, String priority, 
                       LocalDateTime updatedAt, String updatedBy) {
            this.taskId = taskId;
            this.title = title;
            this.description = description;
            this.userId = userId;
            this.projectId = projectId;
            this.status = status;
            this.priority = priority;
            this.updatedAt = updatedAt;
            this.updatedBy = updatedBy;
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
        
        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
        
        public String getUpdatedBy() {
            return updatedBy;
        }
    }
}
