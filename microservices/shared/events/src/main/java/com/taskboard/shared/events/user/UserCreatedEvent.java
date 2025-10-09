package com.taskboard.shared.events.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.taskboard.shared.events.BaseEvent;

/**
 * Событие создания пользователя
 */
public class UserCreatedEvent extends BaseEvent {
    
    @JsonProperty("userId")
    private final Long userId;
    
    @JsonProperty("username")
    private final String username;
    
    @JsonProperty("email")
    private final String email;
    
    @JsonProperty("firstName")
    private final String firstName;
    
    @JsonProperty("lastName")
    private final String lastName;
    
    @JsonProperty("role")
    private final String role;
    
    public UserCreatedEvent(Long userId, String username, String email, 
                           String firstName, String lastName, String role) {
        super("UserCreated", "user-service", "1.0.0");
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
    
    public UserCreatedEvent(String eventId, String eventType, String timestamp, 
                           String source, String version, Long userId, String username, 
                           String email, String firstName, String lastName, String role) {
        super(eventId, eventType, java.time.LocalDateTime.parse(timestamp), source, version);
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getRole() {
        return role;
    }
    
    @Override
    public String toString() {
        return "UserCreatedEvent{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                ", eventId='" + getEventId() + '\'' +
                ", eventType='" + getEventType() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", source='" + getSource() + '\'' +
                ", version='" + getVersion() + '\'' +
                '}';
    }
}
