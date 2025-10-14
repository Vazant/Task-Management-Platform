# Event Processing in User Service

## Overview

This package contains the event processing infrastructure for the User Service, including both outgoing events (published by this service) and incoming events (consumed from other services).

## Architecture

### Outgoing Events (Published by User Service)
- `UserCreatedEvent` - Published when a new user is created
- `UserUpdatedEvent` - Published when a user is updated
- `UserDeletedEvent` - Published when a user is deleted
- `UserEventPublisher` - Interface for publishing user events
- `KafkaUserEventPublisher` - Kafka implementation of event publisher

### Incoming Events (Consumed from Other Services)

#### Task Service Events
- `TaskCreatedEvent` - Received when a task is created
- `TaskUpdatedEvent` - Received when a task is updated
- `TaskDeletedEvent` - Received when a task is deleted

#### Project Service Events
- `ProjectCreatedEvent` - Received when a project is created
- `ProjectMemberAddedEvent` - Received when a user is added to a project

### Event Processing Infrastructure
- `IncomingEvent` - Base interface for all incoming events
- `IncomingEventHandler` - Interface for event handlers
- `IncomingEventProcessor` - Routes events to appropriate handlers
- `EventProcessingException` - Exception for event processing failures

## Event Handlers

### TaskCreatedEventHandler
- Updates user task statistics
- Sends notification to user about new task
- Updates user activity tracking

### TaskUpdatedEventHandler
- Updates user task statistics based on status changes
- Sends notification for important status changes
- Handles priority changes and notifications

### ProjectMemberAddedEventHandler
- Updates user project membership statistics
- Sends welcome notification to new project member
- Updates user activity tracking

## Services

### UserStatisticsService
Manages user statistics and activity tracking:
- Task creation statistics
- Task status change statistics
- Project membership statistics
- User activity tracking

### UserNotificationService
Manages user notifications:
- Task-related notifications
- Project-related notifications
- Notification preferences
- Multi-channel notifications (email, push, in-app)

## Configuration

### IncomingEventConfig
- Configures ObjectMapper for event serialization/deserialization
- Registers all event handlers with IncomingEventProcessor

### KafkaIncomingEventConsumer
- Consumes events from Kafka topics
- Routes events to IncomingEventProcessor
- Handles event parsing and error handling

## TODO Items

### High Priority
- [ ] Implement event deduplication based on event ID
- [ ] Add dead letter queue for failed events
- [ ] Implement retry mechanism with exponential backoff
- [ ] Add event validation and schema evolution
- [ ] Implement user statistics entity and repository

### Medium Priority
- [ ] Add notification preferences per user
- [ ] Implement notification templates
- [ ] Add notification scheduling and batching
- [ ] Integrate with external notification service
- [ ] Add caching for frequently accessed statistics

### Low Priority
- [ ] Add event metrics and monitoring
- [ ] Implement batch updates for performance
- [ ] Add statistics aggregation and reporting
- [ ] Add comprehensive test coverage
- [ ] Add event schema registry integration

## Event Schema

### Task Events
```json
{
  "eventId": "uuid",
  "eventType": "task.created|task.updated|task.deleted",
  "sourceService": "task-service",
  "timestamp": "2024-01-01T00:00:00Z",
  "version": "1.0",
  "data": {
    "taskId": 1,
    "title": "Task Title",
    "description": "Task Description",
    "userId": 1,
    "projectId": 1,
    "status": "TODO|IN_PROGRESS|DONE",
    "priority": "LOW|MEDIUM|HIGH|CRITICAL"
  }
}
```

### Project Events
```json
{
  "eventId": "uuid",
  "eventType": "project.created|project.member.added",
  "sourceService": "project-service",
  "timestamp": "2024-01-01T00:00:00Z",
  "version": "1.0",
  "data": {
    "projectId": 1,
    "name": "Project Name",
    "userId": 1,
    "userRole": "ADMIN|MEMBER|VIEWER"
  }
}
```

## Testing

### Unit Tests
- Event handler tests with mocked dependencies
- Service tests for statistics and notifications
- Event processor tests

### Integration Tests
- Kafka consumer tests with Testcontainers
- End-to-end event processing tests
- Database integration tests for statistics

## Monitoring

### Metrics
- Event processing rate
- Event processing errors
- Handler execution time
- Notification delivery rate

### Logging
- Event received/processed logs
- Error logs with context
- Performance logs for slow handlers

## Security

### Event Validation
- Schema validation for incoming events
- Source service verification
- Event signature validation (future)

### Access Control
- Service-to-service authentication
- Event topic access control
- Handler permission checks
