package com.taskboard.userservice.domain.event.project;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.taskboard.userservice.domain.event.IncomingEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event received when a user is added as a member to a project.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberAddedEvent implements IncomingEvent<ProjectMemberAddedEvent.ProjectMemberData> {
    
    public static final String EVENT_TYPE = "project.member.added";
    public static final String CURRENT_VERSION = "1.0";
    
    private UUID eventId;
    private String eventType;
    private String sourceService;
    private LocalDateTime timestamp;
    private String version;
    private ProjectMemberData data;
    
    /**
     * Project member data payload for ProjectMemberAddedEvent.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectMemberData {
        @NotNull
        private Long projectId;
        
        @NotBlank
        private String projectName;
        
        @NotNull
        private Long userId;
        
        @NotBlank
        private String userRole;
        
        @NotBlank
        private String addedBy;
        
        @NotNull
        private LocalDateTime addedAt;
        
        private String invitationToken;
    }
}
