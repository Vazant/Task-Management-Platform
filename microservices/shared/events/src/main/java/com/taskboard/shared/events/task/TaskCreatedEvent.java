package com.taskboard.shared.events.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.taskboard.shared.events.BaseEvent;

/**
 * Событие создания задачи
 */
public class TaskCreatedEvent extends BaseEvent {
    
    @JsonProperty("taskId")
    private final Long taskId;
    
    @JsonProperty("title")
    private final String title;
    
    @JsonProperty("description")
    private final String description;
    
    @JsonProperty("status")
    private final String status;
    
    @JsonProperty("priority")
    private final String priority;
    
    @JsonProperty("userId")
    private final Long userId;
    
    @JsonProperty("projectId")
    private final Long projectId;
    
    public TaskCreatedEvent(Long taskId, String title, String description, 
                           String status, String priority, Long userId, Long projectId) {
        super("TaskCreated", "task-service", "1.0.0");
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.userId = userId;
        this.projectId = projectId;
    }
    
    public TaskCreatedEvent(String eventId, String eventType, String timestamp, 
                           String source, String version, Long taskId, String title, 
                           String description, String status, String priority, 
                           Long userId, Long projectId) {
        super(eventId, eventType, java.time.LocalDateTime.parse(timestamp), source, version);
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.userId = userId;
        this.projectId = projectId;
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
    
    public String getStatus() {
        return status;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Long getProjectId() {
        return projectId;
    }
    
    @Override
    public String toString() {
        return "TaskCreatedEvent{" +
                "taskId=" + taskId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", userId=" + userId +
                ", projectId=" + projectId +
                ", eventId='" + getEventId() + '\'' +
                ", eventType='" + getEventType() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", source='" + getSource() + '\'' +
                ", version='" + getVersion() + '\'' +
                '}';
    }
}
