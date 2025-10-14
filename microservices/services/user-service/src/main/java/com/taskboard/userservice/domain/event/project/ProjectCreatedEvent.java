package com.taskboard.userservice.domain.event.project;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taskboard.userservice.domain.event.IncomingEvent;

/**
 * Event received when a project is created in the Project Service.
 * 
 * TODO: Add project settings and configuration
 * TODO: Add team member information
 * TODO: Add project templates and categories
 */
public class ProjectCreatedEvent implements IncomingEvent<ProjectCreatedEvent.ProjectData> {
    
    private final UUID eventId;
    private final String eventType;
    private final String sourceService;
    private final LocalDateTime timestamp;
    private final String version;
    private final ProjectData data;
    
    public ProjectCreatedEvent(UUID eventId, String eventType, String sourceService, 
                             LocalDateTime timestamp, String version, ProjectData data) {
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
    public ProjectData getData() {
        return data;
    }
    
    /**
     * Project data payload for ProjectCreatedEvent.
     */
    public static class ProjectData {
        private final Long projectId;
        private final String name;
        private final String description;
        private final Long ownerId;
        private final String status;
        private final String priority;
        private final LocalDateTime startDate;
        private final LocalDateTime endDate;
        private final LocalDateTime createdAt;
        
        public ProjectData(Long projectId, String name, String description, Long ownerId, 
                          String status, String priority, LocalDateTime startDate, 
                          LocalDateTime endDate, LocalDateTime createdAt) {
            this.projectId = projectId;
            this.name = name;
            this.description = description;
            this.ownerId = ownerId;
            this.status = status;
            this.priority = priority;
            this.startDate = startDate;
            this.endDate = endDate;
            this.createdAt = createdAt;
        }
        
        public Long getProjectId() {
            return projectId;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public Long getOwnerId() {
            return ownerId;
        }
        
        public String getStatus() {
            return status;
        }
        
        public String getPriority() {
            return priority;
        }
        
        public LocalDateTime getStartDate() {
            return startDate;
        }
        
        public LocalDateTime getEndDate() {
            return endDate;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
}
