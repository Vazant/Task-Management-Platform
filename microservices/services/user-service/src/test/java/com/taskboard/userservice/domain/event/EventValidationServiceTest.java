package com.taskboard.userservice.domain.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class EventValidationServiceTest {
    
    private EventValidationService validationService;
    
    @BeforeEach
    void setUp() {
        validationService = new EventValidationService();
    }
    
    @Test
    void shouldValidateValidEvent() {
        // Given
        TestEvent event = new TestEvent(
            UUID.randomUUID(),
            "task.created",
            "task-service",
            LocalDateTime.now(),
            "1.0",
            "test data"
        );
        
        // When & Then - should not throw exception
        validationService.validateEvent(event);
    }
    
    @Test
    void shouldRejectEventWithNullId() {
        // Given
        TestEvent event = new TestEvent(
            null,
            "task.created",
            "task-service",
            LocalDateTime.now(),
            "1.0",
            "test data"
        );
        
        // When & Then
        assertThatThrownBy(() -> validationService.validateEvent(event))
            .isInstanceOf(EventValidationException.class)
            .hasMessageContaining("Event ID cannot be null");
    }
    
    @Test
    void shouldRejectEventWithInvalidEventType() {
        // Given
        TestEvent event = new TestEvent(
            UUID.randomUUID(),
            "invalid-event-type",
            "task-service",
            LocalDateTime.now(),
            "1.0",
            "test data"
        );
        
        // When & Then
        assertThatThrownBy(() -> validationService.validateEvent(event))
            .isInstanceOf(EventValidationException.class)
            .hasMessageContaining("Event type must follow pattern");
    }
    
    @Test
    void shouldRejectEventWithInvalidSourceService() {
        // Given
        TestEvent event = new TestEvent(
            UUID.randomUUID(),
            "task.created",
            "invalid",
            LocalDateTime.now(),
            "1.0",
            "test data"
        );
        
        // When & Then
        assertThatThrownBy(() -> validationService.validateEvent(event))
            .isInstanceOf(EventValidationException.class)
            .hasMessageContaining("Source service must follow pattern");
    }
    
    @Test
    void shouldRejectEventWithOldTimestamp() {
        // Given
        TestEvent event = new TestEvent(
            UUID.randomUUID(),
            "task.created",
            "task-service",
            LocalDateTime.now().minusDays(2),
            "1.0",
            "test data"
        );
        
        // When & Then
        assertThatThrownBy(() -> validationService.validateEvent(event))
            .isInstanceOf(EventValidationException.class)
            .hasMessageContaining("Event timestamp cannot be more than 24 hours in the past");
    }
    
    @Test
    void shouldRejectEventWithFutureTimestamp() {
        // Given
        TestEvent event = new TestEvent(
            UUID.randomUUID(),
            "task.created",
            "task-service",
            LocalDateTime.now().plusHours(1),
            "1.0",
            "test data"
        );
        
        // When & Then
        assertThatThrownBy(() -> validationService.validateEvent(event))
            .isInstanceOf(EventValidationException.class)
            .hasMessageContaining("Event timestamp cannot be more than 5 minutes in the future");
    }
    
    @Test
    void shouldRejectEventWithUnsupportedVersion() {
        // Given
        TestEvent event = new TestEvent(
            UUID.randomUUID(),
            "task.created",
            "task-service",
            LocalDateTime.now(),
            "99.0",
            "test data"
        );
        
        // When & Then
        assertThatThrownBy(() -> validationService.validateEvent(event))
            .isInstanceOf(EventValidationException.class)
            .hasMessageContaining("Unsupported event version");
    }
    
    @Test
    void shouldRejectEventWithNullData() {
        // Given
        TestEvent event = new TestEvent(
            UUID.randomUUID(),
            "task.created",
            "task-service",
            LocalDateTime.now(),
            "1.0",
            null
        );
        
        // When & Then
        assertThatThrownBy(() -> validationService.validateEvent(event))
            .isInstanceOf(EventValidationException.class)
            .hasMessageContaining("Event data cannot be null");
    }
    
    @Test
    void shouldCheckVersionSupport() {
        // When & Then
        assertThat(validationService.isVersionSupported("1.0")).isTrue();
        assertThat(validationService.isVersionSupported("2.0")).isTrue();
        assertThat(validationService.isVersionSupported("99.0")).isFalse();
        assertThat(validationService.isVersionSupported(null)).isFalse();
    }
    
    @Test
    void shouldReturnLatestSupportedVersion() {
        // When
        String latestVersion = validationService.getLatestSupportedVersion();
        
        // Then
        assertThat(latestVersion).isEqualTo("2.0");
    }
    
    @Test
    void shouldReturnSupportedVersions() {
        // When
        String[] supportedVersions = validationService.getSupportedVersions();
        
        // Then
        assertThat(supportedVersions).containsExactly("1.0", "1.1", "2.0");
    }
    
    /**
     * Test implementation of IncomingEvent for testing purposes.
     */
    private static class TestEvent implements IncomingEvent<String> {
        private final UUID eventId;
        private final String eventType;
        private final String sourceService;
        private final LocalDateTime timestamp;
        private final String version;
        private final String data;
        
        public TestEvent(UUID eventId, String eventType, String sourceService, 
                        LocalDateTime timestamp, String version, String data) {
            this.eventId = eventId;
            this.eventType = eventType;
            this.sourceService = sourceService;
            this.timestamp = timestamp;
            this.version = version;
            this.data = data;
        }
        
        @Override
        public UUID getEventId() {
            return eventId;
        }
        
        @Override
        public String getEventType() {
            return eventType;
        }
        
        @Override
        public String getSourceService() {
            return sourceService;
        }
        
        @Override
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        @Override
        public String getVersion() {
            return version;
        }
        
        @Override
        public String getData() {
            return data;
        }
    }
}
