package com.taskboard.userservice.domain.event.task;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taskboard.userservice.domain.event.IncomingEvent;

/**
 * Event received when a task is deleted in the Task Service.
 * 
 * TODO: Add soft delete information
 * TODO: Add deletion reason
 * TODO: Add cascade deletion information
 */
public class TaskDeletedEvent implements IncomingEvent<TaskDeletedEvent.TaskData> {
    
    private final UUID eventId;
    private final String eventType;
    private final String sourceService;
    private final LocalDateTime timestamp;
    private final String version;
    private final TaskData data;
    
    public TaskDeletedEvent(UUID eventId, String eventType, String sourceService, 
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
     * Task data payload for TaskDeletedEvent.
     */
    public static class TaskData {
        private final Long taskId;
        private final String title;
        private final Long userId;
        private final Long projectId;
        private final LocalDateTime deletedAt;
        private final String deletedBy;
        private final String reason;
        
        public TaskData(Long taskId, String title, Long userId, Long projectId, 
                       LocalDateTime deletedAt, String deletedBy, String reason) {
            this.taskId = taskId;
            this.title = title;
            this.userId = userId;
            this.projectId = projectId;
            this.deletedAt = deletedAt;
            this.deletedBy = deletedBy;
            this.reason = reason;
        }
        
        public Long getTaskId() {
            return taskId;
        }
        
        public String getTitle() {
            return title;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public Long getProjectId() {
            return projectId;
        }
        
        public LocalDateTime getDeletedAt() {
            return deletedAt;
        }
        
        public String getDeletedBy() {
            return deletedBy;
        }
        
        public String getReason() {
            return reason;
        }
    }
}
