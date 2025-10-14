package com.taskboard.userservice.domain.event.project;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taskboard.userservice.domain.event.IncomingEvent;

/**
 * Event received when a user is added as a member to a project.
 * 
 * TODO: Add role information (admin, member, viewer)
 * TODO: Add permissions and access levels
 * TODO: Add invitation information
 */
public class ProjectMemberAddedEvent implements IncomingEvent<ProjectMemberAddedEvent.ProjectMemberData> {
    
    private final UUID eventId;
    private final String eventType;
    private final String sourceService;
    private final LocalDateTime timestamp;
    private final String version;
    private final ProjectMemberData data;
    
    public ProjectMemberAddedEvent(UUID eventId, String eventType, String sourceService, 
                                 LocalDateTime timestamp, String version, ProjectMemberData data) {
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
    public ProjectMemberData getData() {
        return data;
    }
    
    /**
     * Project member data payload for ProjectMemberAddedEvent.
     */
    public static class ProjectMemberData {
        private final Long projectId;
        private final String projectName;
        private final Long userId;
        private final String userRole;
        private final String addedBy;
        private final LocalDateTime addedAt;
        private final String invitationToken;
        
        public ProjectMemberData(Long projectId, String projectName, Long userId, 
                               String userRole, String addedBy, LocalDateTime addedAt, 
                               String invitationToken) {
            this.projectId = projectId;
            this.projectName = projectName;
            this.userId = userId;
            this.userRole = userRole;
            this.addedBy = addedBy;
            this.addedAt = addedAt;
            this.invitationToken = invitationToken;
        }
        
        public Long getProjectId() {
            return projectId;
        }
        
        public String getProjectName() {
            return projectName;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public String getUserRole() {
            return userRole;
        }
        
        public String getAddedBy() {
            return addedBy;
        }
        
        public LocalDateTime getAddedAt() {
            return addedAt;
        }
        
        public String getInvitationToken() {
            return invitationToken;
        }
    }
}
