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
 * Event received when a project is created in the Project Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreatedEvent implements IncomingEvent<ProjectCreatedEvent.ProjectData> {
    
    public static final String EVENT_TYPE = "project.created";
    public static final String CURRENT_VERSION = "1.0";
    
    private UUID eventId;
    private String eventType;
    private String sourceService;
    private LocalDateTime timestamp;
    private String version;
    private ProjectData data;
    
    /**
     * Project data payload for ProjectCreatedEvent.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectData {
        @NotNull
        private Long projectId;
        
        @NotBlank
        private String name;
        
        private String description;
        
        @NotNull
        private Long ownerId;
        
        @NotBlank
        private String status;
        
        @NotBlank
        private String priority;
        
        private LocalDateTime startDate;
        
        private LocalDateTime endDate;
        
        @NotNull
        private LocalDateTime createdAt;
    }
}
