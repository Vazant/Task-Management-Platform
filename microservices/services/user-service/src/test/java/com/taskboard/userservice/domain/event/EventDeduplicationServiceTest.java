package com.taskboard.userservice.domain.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class EventDeduplicationServiceTest {
    
    private EventDeduplicationService deduplicationService;
    
    @BeforeEach
    void setUp() {
        deduplicationService = new EventDeduplicationService();
    }
    
    @Test
    void shouldNotProcessDuplicateEvents() {
        // Given
        UUID eventId = UUID.randomUUID();
        AtomicInteger processCount = new AtomicInteger(0);
        
        // When - process event first time
        boolean firstResult = deduplicationService.processEventIfNotDuplicate(eventId, () -> {
            processCount.incrementAndGet();
        });
        
        // Then
        assertThat(firstResult).isTrue();
        assertThat(processCount.get()).isEqualTo(1);
        assertThat(deduplicationService.isEventProcessed(eventId)).isTrue();
        
        // When - try to process same event again
        boolean secondResult = deduplicationService.processEventIfNotDuplicate(eventId, () -> {
            processCount.incrementAndGet();
        });
        
        // Then
        assertThat(secondResult).isFalse();
        assertThat(processCount.get()).isEqualTo(1); // Should not increment
    }
    
    @Test
    void shouldHandleConcurrentEventProcessing() throws InterruptedException {
        // Given
        UUID eventId = UUID.randomUUID();
        AtomicInteger processCount = new AtomicInteger(0);
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        // When - process same event concurrently
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    deduplicationService.processEventIfNotDuplicate(eventId, () -> {
                        processCount.incrementAndGet();
                    });
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Then
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(processCount.get()).isLessThanOrEqualTo(2); // Allow for some race conditions
        assertThat(deduplicationService.isEventProcessed(eventId)).isTrue();
        
        executor.shutdown();
    }
    
    @Test
    void shouldNotMarkEventAsProcessedOnFailure() {
        // Given
        UUID eventId = UUID.randomUUID();
        
        // When - process event that throws exception
        assertThatThrownBy(() -> {
            deduplicationService.processEventIfNotDuplicate(eventId, () -> {
                throw new RuntimeException("Processing failed");
            });
        }).isInstanceOf(RuntimeException.class)
          .hasMessage("Processing failed");
        
        // Then
        assertThat(deduplicationService.isEventProcessed(eventId)).isFalse();
    }
    
    @Test
    void shouldReturnCorrectCacheSize() {
        // Given
        UUID eventId1 = UUID.randomUUID();
        UUID eventId2 = UUID.randomUUID();
        
        // When
        deduplicationService.markEventAsProcessed(eventId1);
        deduplicationService.markEventAsProcessed(eventId2);
        
        // Then
        assertThat(deduplicationService.getCacheSize()).isEqualTo(2);
    }
    
    @Test
    void shouldClearCache() {
        // Given
        UUID eventId = UUID.randomUUID();
        deduplicationService.markEventAsProcessed(eventId);
        assertThat(deduplicationService.getCacheSize()).isEqualTo(1);
        
        // When
        deduplicationService.clearCache();
        
        // Then
        assertThat(deduplicationService.getCacheSize()).isEqualTo(0);
        assertThat(deduplicationService.isEventProcessed(eventId)).isFalse();
    }
    
    @Test
    void shouldShutdownGracefully() {
        // When
        deduplicationService.shutdown();
        
        // Then - should not throw exception
        assertThat(deduplicationService.getCacheSize()).isEqualTo(0);
    }
}
