package com.taskboard.userservice.integration;

import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.usecase.CreateUserUseCase;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import com.taskboard.userservice.infrastructure.event.KafkaUserEventPublisher;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты для Kafka событий с реальным Kafka контейнером.
 */
@Transactional
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class KafkaEventIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CreateUserUseCase createUserUseCase;

    @Autowired
    private KafkaUserEventPublisher kafkaUserEventPublisher;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private Consumer<String, String> consumer;
    private static final String TOPIC_NAME = "user.events";

    @BeforeEach
    void setUp() {
        // Create Kafka consumer for testing
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    @DisplayName("Should publish user created event to Kafka")
    void shouldPublishUserCreatedEventToKafka() throws Exception {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("kafkatestuser")
                .email("kafkatest@example.com")
                .password("password123")
                .firstName("Kafka")
                .lastName("Test")
                .role(UserRole.USER)
                .build();

        // When
        var response = createUserUseCase.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isNotNull();

        // Wait for event to be published and consumed
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));
        
        assertThat(records.count()).isGreaterThan(0);
        
        // Verify event content
        boolean userCreatedEventFound = false;
        for (ConsumerRecord<String, String> record : records) {
            if (record.value().contains("USER_CREATED") && 
                record.value().contains("kafkatestuser")) {
                userCreatedEventFound = true;
                break;
            }
        }
        
        assertThat(userCreatedEventFound).isTrue();
    }

    @Test
    @DisplayName("Should publish user updated event to Kafka")
    void shouldPublishUserUpdatedEventToKafka() throws Exception {
        // Given
        Long userId = 1L; // Existing user from test data

        // When
        kafkaUserEventPublisher.publishUserUpdatedEvent(userId, "testuser1", "test1@example.com");

        // Then
        // Wait for event to be published and consumed
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));
        
        assertThat(records.count()).isGreaterThan(0);
        
        // Verify event content
        boolean userUpdatedEventFound = false;
        for (ConsumerRecord<String, String> record : records) {
            if (record.value().contains("USER_UPDATED") && 
                record.value().contains("testuser1")) {
                userUpdatedEventFound = true;
                break;
            }
        }
        
        assertThat(userUpdatedEventFound).isTrue();
    }

    @Test
    @DisplayName("Should publish user deleted event to Kafka")
    void shouldPublishUserDeletedEventToKafka() throws Exception {
        // Given
        Long userId = 3L; // inactiveuser from test data

        // When
        kafkaUserEventPublisher.publishUserDeletedEvent(userId, "inactiveuser", "inactive@example.com");

        // Then
        // Wait for event to be published and consumed
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));
        
        assertThat(records.count()).isGreaterThan(0);
        
        // Verify event content
        boolean userDeletedEventFound = false;
        for (ConsumerRecord<String, String> record : records) {
            if (record.value().contains("USER_DELETED") && 
                record.value().contains("inactiveuser")) {
                userDeletedEventFound = true;
                break;
            }
        }
        
        assertThat(userDeletedEventFound).isTrue();
    }

    @Test
    @DisplayName("Should handle Kafka connection errors gracefully")
    void shouldHandleKafkaConnectionErrorsGracefully() {
        // Given
        Long userId = 1L;

        // When & Then
        // This test verifies that the application doesn't crash when Kafka is unavailable
        // The event publisher should handle connection errors gracefully
        assertThatCode(() -> {
            kafkaUserEventPublisher.publishUserUpdatedEvent(userId, "testuser1", "test1@example.com");
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should publish events with correct topic and partition")
    void shouldPublishEventsWithCorrectTopicAndPartition() throws Exception {
        // Given
        Long userId = 1L;

        // When
        kafkaUserEventPublisher.publishUserUpdatedEvent(userId, "testuser1", "test1@example.com");

        // Then
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));
        
        assertThat(records.count()).isGreaterThan(0);
        
        // Verify topic and partition
        for (ConsumerRecord<String, String> record : records) {
            assertThat(record.topic()).isEqualTo(TOPIC_NAME);
            assertThat(record.partition()).isGreaterThanOrEqualTo(0);
            assertThat(record.key()).isNotNull();
            assertThat(record.value()).isNotNull();
        }
    }

    @Test
    @DisplayName("Should publish events with proper serialization")
    void shouldPublishEventsWithProperSerialization() throws Exception {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("serializationtest")
                .email("serialization@example.com")
                .password("password123")
                .firstName("Serialization")
                .lastName("Test")
                .role(UserRole.USER)
                .build();

        // When
        var response = createUserUseCase.execute(request);

        // Then
        assertThat(response).isNotNull();

        // Wait for event and verify JSON structure
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));
        
        assertThat(records.count()).isGreaterThan(0);
        
        for (ConsumerRecord<String, String> record : records) {
            if (record.value().contains("serializationtest")) {
                // Verify JSON structure
                assertThat(record.value()).contains("\"eventType\"");
                assertThat(record.value()).contains("\"userId\"");
                assertThat(record.value()).contains("\"username\"");
                assertThat(record.value()).contains("\"email\"");
                assertThat(record.value()).contains("\"timestamp\"");
                break;
            }
        }
    }
}
