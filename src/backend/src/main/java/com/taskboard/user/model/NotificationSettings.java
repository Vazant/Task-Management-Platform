package com.taskboard.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Embeddable class representing notification settings.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettings {
    
    @Column(name = "email_notifications")
    private boolean email = true;
    
    @Column(name = "push_notifications")
    private boolean push = true;
    
    @Column(name = "task_update_notifications")
    private boolean taskUpdates = true;
    
    @Column(name = "project_update_notifications")
    private boolean projectUpdates = true;
}