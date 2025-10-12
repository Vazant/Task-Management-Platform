package com.taskboard.userservice.infrastructure.event;

import com.taskboard.userservice.domain.event.UserCreatedEvent;
import com.taskboard.userservice.domain.event.UserDeletedEvent;
import com.taskboard.userservice.domain.event.UserEventPublisher;
import com.taskboard.userservice.domain.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka implementation of UserEventPublisher.
 *
 * <p>This class implements the UserEventPublisher interface using Apache Kafka
 * for publishing user domain events. It provides reliable event delivery with
 * proper error handling and logging.
 *
 * <p>The KafkaUserEventPublisher is responsible for:
 *
 * <ul>
 *   <li>Publishing user events to Kafka topics
 *   <li>Handling Kafka send failures and retries
 *   <li>Logging event publication for monitoring
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaUserEventPublisher implements UserEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.user-created:user.created}")
    private String userCreatedTopic;

    @Value("${kafka.topics.user-updated:user.updated}")
    private String userUpdatedTopic;

    @Value("${kafka.topics.user-deleted:user.deleted}")
    private String userDeletedTopic;

    @Override
    public void publishUserCreated(UserCreatedEvent event) {
        log.info("Publishing user created event: userId={}, username={}", 
                event.getUserId(), event.getUsername());
        
        CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(userCreatedTopic, event.getUserId().toString(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("User created event published successfully: userId={}, offset={}", 
                        event.getUserId(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish user created event: userId={}", 
                        event.getUserId(), ex);
            }
        });
    }

    @Override
    public void publishUserUpdated(UserUpdatedEvent event) {
        log.info("Publishing user updated event: userId={}, username={}", 
                event.getUserId(), event.getUsername());
        
        CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(userUpdatedTopic, event.getUserId().toString(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("User updated event published successfully: userId={}, offset={}", 
                        event.getUserId(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish user updated event: userId={}", 
                        event.getUserId(), ex);
            }
        });
    }

    @Override
    public void publishUserDeleted(UserDeletedEvent event) {
        log.info("Publishing user deleted event: userId={}, username={}", 
                event.getUserId(), event.getUsername());
        
        CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(userDeletedTopic, event.getUserId().toString(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("User deleted event published successfully: userId={}, offset={}", 
                        event.getUserId(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish user deleted event: userId={}", 
                        event.getUserId(), ex);
            }
        });
    }
}

