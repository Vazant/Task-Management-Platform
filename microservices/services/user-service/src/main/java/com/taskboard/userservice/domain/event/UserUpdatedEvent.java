package com.taskboard.userservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Domain event for user updates.
 *
 * <p>This event is published when a user's information is updated in the system.
 * It contains the user ID and the fields that were changed for other services
 * to react to this event.
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatedEvent {

    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String status;
    private Map<String, Object> changedFields;
    private LocalDateTime updatedAt;
    private String eventId;
    private LocalDateTime eventTimestamp;

    /**
     * Creates a UserUpdatedEvent with current timestamp.
     *
     * @param userId the user ID
     * @param username the username
     * @param email the email
     * @param firstName the first name
     * @param lastName the last name
     * @param role the user role
     * @param status the user status
     * @param changedFields the fields that were changed
     * @param updatedAt the update timestamp
     * @return the UserUpdatedEvent
     */
    public static UserUpdatedEvent of(Long userId, String username, String email, 
                                    String firstName, String lastName, String role, 
                                    String status, Map<String, Object> changedFields, 
                                    LocalDateTime updatedAt) {
        return UserUpdatedEvent.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .status(status)
                .changedFields(changedFields)
                .updatedAt(updatedAt)
                .eventId(java.util.UUID.randomUUID().toString())
                .eventTimestamp(LocalDateTime.now())
                .build();
    }
}

