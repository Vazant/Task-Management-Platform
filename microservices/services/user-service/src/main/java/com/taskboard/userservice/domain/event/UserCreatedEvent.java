package com.taskboard.userservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Domain event for user creation.
 *
 * <p>This event is published when a new user is successfully created in the system.
 * It contains all the necessary information about the created user for other services
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
public class UserCreatedEvent {

    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private LocalDateTime createdAt;
    private String eventId;
    private LocalDateTime eventTimestamp;

    /**
     * Creates a UserCreatedEvent with current timestamp.
     *
     * @param userId the user ID
     * @param username the username
     * @param email the email
     * @param firstName the first name
     * @param lastName the last name
     * @param role the user role
     * @param createdAt the creation timestamp
     * @return the UserCreatedEvent
     */
    public static UserCreatedEvent of(Long userId, String username, String email, 
                                    String firstName, String lastName, String role, 
                                    LocalDateTime createdAt) {
        return UserCreatedEvent.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .createdAt(createdAt)
                .eventId(java.util.UUID.randomUUID().toString())
                .eventTimestamp(LocalDateTime.now())
                .build();
    }
}



