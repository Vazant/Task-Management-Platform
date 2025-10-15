package com.taskboard.userservice.domain.event.task;

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
 * Event received when a task is updated in the Task Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdatedEvent implements IncomingEvent<TaskUpdatedEvent.TaskData> {
    
    public static final String EVENT_TYPE = "task.updated";
    public static final String CURRENT_VERSION = "1.0";
    
    private UUID eventId;
    private String eventType;
    private String sourceService;
    private LocalDateTime timestamp;
    private String version;
    private TaskData data;
    
    /**
     * Task data payload for TaskUpdatedEvent.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskData {
        @NotNull
        private Long taskId;
        
        @NotBlank
        private String title;
        
        private String description;
        
        @NotNull
        private Long userId;
        
        private Long projectId;
        
        @NotBlank
        private String status;
        
        @NotBlank
        private String priority;
        
        @NotNull
        private LocalDateTime updatedAt;
        
        @NotBlank
        private String updatedBy;
    }
}
