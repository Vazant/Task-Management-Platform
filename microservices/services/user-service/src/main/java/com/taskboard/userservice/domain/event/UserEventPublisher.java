package com.taskboard.userservice.domain.event;

/**
 * Interface for publishing user domain events.
 *
 * <p>This interface defines the contract for publishing user-related domain events
 * to external systems, such as message queues or event stores. It follows the
 * Domain-Driven Design pattern for event publishing.
 *
 * <p>The UserEventPublisher is responsible for:
 *
 * <ul>
 *   <li>Publishing user lifecycle events (created, updated, deleted)
 *   <li>Ensuring event delivery and reliability
 *   <li>Handling event serialization and routing
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserEventPublisher {

    /**
     * Publishes a user created event.
     *
     * @param event the user created event
     */
    void publishUserCreated(UserCreatedEvent event);

    /**
     * Publishes a user updated event.
     *
     * @param event the user updated event
     */
    void publishUserUpdated(UserUpdatedEvent event);

    /**
     * Publishes a user deleted event.
     *
     * @param event the user deleted event
     */
    void publishUserDeleted(UserDeletedEvent event);
}

