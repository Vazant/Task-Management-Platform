package com.taskboard.userservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Domain event for user deletion.
 *
 * <p>This event is published when a user is deleted from the system.
 * It contains the user ID and deletion timestamp for other services
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
@EqualsAndHashCode(of = {"userId", "username", "email"}) // Use significant fields for events
@ToString(exclude = {"eventId"}) // Exclude technical fields
public class UserDeletedEvent {

    private Long userId;
    private String username;
    private String email;
    private LocalDateTime deletedAt;
    private String eventId;
    private LocalDateTime eventTimestamp;

    /**
     * Creates a UserDeletedEvent with current timestamp.
     *
     * @param userId the user ID
     * @param username the username
     * @param email the email
     * @param deletedAt the deletion timestamp
     * @return the UserDeletedEvent
     */
    public static UserDeletedEvent of(Long userId, String username, String email, 
                                    LocalDateTime deletedAt) {
        return UserDeletedEvent.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .deletedAt(deletedAt)
                .eventId(java.util.UUID.randomUUID().toString())
                .eventTimestamp(LocalDateTime.now())
                .build();
    }
}



