package com.taskboard.userservice.application.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.taskboard.userservice.application.service.UserNotificationService;
import com.taskboard.userservice.application.service.UserStatisticsService;
import com.taskboard.userservice.domain.event.IncomingEvent;
import com.taskboard.userservice.domain.event.IncomingEventHandler;
import com.taskboard.userservice.domain.event.task.TaskUpdatedEvent;

/**
 * Handles TaskUpdatedEvent from Task Service.
 * 
 * TODO: Update user statistics based on status changes
 * TODO: Send notification for important status changes
 * TODO: Update user activity tracking
 * TODO: Handle priority changes and notifications
 */
@Component
public class TaskUpdatedEventHandler implements IncomingEventHandler<TaskUpdatedEvent.TaskData> {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskUpdatedEventHandler.class);
    
    private static final String EVENT_TYPE = "task.updated";
    private static final String SOURCE_SERVICE = "task-service";
    
    private final UserStatisticsService userStatisticsService;
    private final UserNotificationService userNotificationService;
    
    public TaskUpdatedEventHandler(UserStatisticsService userStatisticsService, 
                                 UserNotificationService userNotificationService) {
        this.userStatisticsService = userStatisticsService;
        this.userNotificationService = userNotificationService;
    }
    
    @Override
    public void handle(IncomingEvent<TaskUpdatedEvent.TaskData> event) {
        logger.info("Processing TaskUpdatedEvent for user: {}", event.getData().getUserId());
        
        try {
            TaskUpdatedEvent.TaskData taskData = event.getData();
            
            // 1. Update user task statistics based on status change
            userStatisticsService.updateTaskStatusStatistics(taskData.getUserId(), taskData.getStatus());
            
            // 2. Check for important status changes and send notification
            if (userNotificationService.shouldNotifyUser(taskData.getUserId(), "task_status_changed")) {
                userNotificationService.sendTaskStatusChangeNotification(
                    taskData.getUserId(), 
                    taskData.getTitle(), 
                    taskData.getTaskId(),
                    "UNKNOWN", // TODO: Get previous status from event
                    taskData.getStatus()
                );
            }
            
            // 3. Update user activity
            userStatisticsService.updateUserActivity(taskData.getUserId(), "task_updated");
            
            // 4. Handle priority changes
            if (userNotificationService.shouldNotifyUser(taskData.getUserId(), "task_priority_changed")) {
                userNotificationService.sendPriorityChangeNotification(
                    taskData.getUserId(), 
                    taskData.getTitle(), 
                    taskData.getTaskId(),
                    "UNKNOWN", // TODO: Get previous priority from event
                    taskData.getPriority()
                );
            }
            
            logger.info("Successfully processed TaskUpdatedEvent for user: {}", taskData.getUserId());
            
        } catch (Exception e) {
            logger.error("Failed to process TaskUpdatedEvent for user: {}", 
                event.getData().getUserId(), e);
            throw e;
        }
    }
    
    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
    
    @Override
    public String getSourceService() {
        return SOURCE_SERVICE;
    }
    
}
