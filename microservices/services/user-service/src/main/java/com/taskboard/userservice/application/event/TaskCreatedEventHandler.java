package com.taskboard.userservice.application.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.taskboard.userservice.application.service.UserNotificationService;
import com.taskboard.userservice.application.service.UserStatisticsService;
import com.taskboard.userservice.domain.event.IncomingEvent;
import com.taskboard.userservice.domain.event.IncomingEventHandler;
import com.taskboard.userservice.domain.event.task.TaskCreatedEvent;

/**
 * Handles TaskCreatedEvent from Task Service.
 * 
 * TODO: Update user statistics (task count)
 * TODO: Send notification to user about new task
 * TODO: Update user activity tracking
 * TODO: Check if user needs to be notified about task assignment
 */
@Component
public class TaskCreatedEventHandler implements IncomingEventHandler<TaskCreatedEvent.TaskData> {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskCreatedEventHandler.class);
    
    private static final String EVENT_TYPE = "task.created";
    private static final String SOURCE_SERVICE = "task-service";
    
    private final UserStatisticsService userStatisticsService;
    private final UserNotificationService userNotificationService;
    
    public TaskCreatedEventHandler(UserStatisticsService userStatisticsService, 
                                 UserNotificationService userNotificationService) {
        this.userStatisticsService = userStatisticsService;
        this.userNotificationService = userNotificationService;
    }
    
    @Override
    public void handle(IncomingEvent<TaskCreatedEvent.TaskData> event) {
        logger.info("Processing TaskCreatedEvent for user: {}", event.getData().getUserId());
        
        try {
            TaskCreatedEvent.TaskData taskData = event.getData();
            
            // 1. Update user task statistics
            userStatisticsService.updateTaskCreatedStatistics(taskData.getUserId());
            
            // 2. Check if user should be notified and send notification
            if (userNotificationService.shouldNotifyUser(taskData.getUserId(), "task_created")) {
                userNotificationService.sendTaskCreatedNotification(
                    taskData.getUserId(), 
                    taskData.getTitle(), 
                    taskData.getTaskId()
                );
            }
            
            // 3. Update user activity
            userStatisticsService.updateUserActivity(taskData.getUserId(), "task_created");
            
            logger.info("Successfully processed TaskCreatedEvent for user: {}", taskData.getUserId());
            
        } catch (Exception e) {
            logger.error("Failed to process TaskCreatedEvent for user: {}", 
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
