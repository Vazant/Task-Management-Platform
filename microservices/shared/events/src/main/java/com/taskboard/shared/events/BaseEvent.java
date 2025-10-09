package com.taskboard.shared.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Базовый класс для всех событий в системе
 */
public abstract class BaseEvent {
    
    @JsonProperty("eventId")
    private final String eventId;
    
    @JsonProperty("eventType")
    private final String eventType;
    
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private final LocalDateTime timestamp;
    
    @JsonProperty("source")
    private final String source;
    
    @JsonProperty("version")
    private final String version;
    
    protected BaseEvent(String eventType, String source, String version) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.source = source;
        this.version = version;
    }
    
    protected BaseEvent(String eventId, String eventType, LocalDateTime timestamp, 
                       String source, String version) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.source = source;
        this.version = version;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getSource() {
        return source;
    }
    
    public String getVersion() {
        return version;
    }
    
    @Override
    public String toString() {
        return "BaseEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", source='" + source + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
