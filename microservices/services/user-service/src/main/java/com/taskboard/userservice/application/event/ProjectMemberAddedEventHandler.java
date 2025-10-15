package com.taskboard.userservice.application.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.taskboard.userservice.application.service.UserNotificationService;
import com.taskboard.userservice.application.service.UserStatisticsService;
import com.taskboard.userservice.domain.event.IncomingEvent;
import com.taskboard.userservice.domain.event.IncomingEventHandler;
import com.taskboard.userservice.domain.event.project.ProjectMemberAddedEvent;

/**
 * Handles ProjectMemberAddedEvent from Project Service.
 * 
 * TODO: Update user project membership
 * TODO: Send welcome notification to new project member
 * TODO: Update user permissions and roles
 * TODO: Track project membership activity
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectMemberAddedEventHandler implements IncomingEventHandler<ProjectMemberAddedEvent.ProjectMemberData> {
    
    private static final String EVENT_TYPE = "project.member.added";
    private static final String SOURCE_SERVICE = "project-service";
    
    private final UserStatisticsService userStatisticsService;
    private final UserNotificationService userNotificationService;
    
    @Override
    public void handle(IncomingEvent<ProjectMemberAddedEvent.ProjectMemberData> event) {
        log.info("Processing ProjectMemberAddedEvent for user: {}", event.getData().getUserId());
        
        try {
            ProjectMemberAddedEvent.ProjectMemberData memberData = event.getData();
            
            // 1. Update user project membership statistics
            userStatisticsService.updateProjectMembershipStatistics(
                memberData.getUserId(), 
                memberData.getProjectId(), 
                memberData.getUserRole()
            );
            
            // 2. Send welcome notification
            if (userNotificationService.shouldNotifyUser(memberData.getUserId(), "project_member_added")) {
                userNotificationService.sendProjectWelcomeNotification(
                    memberData.getUserId(), 
                    memberData.getProjectName(), 
                    memberData.getProjectId(), 
                    memberData.getUserRole()
                );
            }
            
            // 3. Update user activity
            userStatisticsService.updateUserActivity(memberData.getUserId(), "project_member_added");
            
            log.info("Successfully processed ProjectMemberAddedEvent for user: {}", memberData.getUserId());
            
        } catch (Exception e) {
            log.error("Failed to process ProjectMemberAddedEvent for user: {}", 
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
