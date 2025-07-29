package com.taskboard.user.dto;

import lombok.*;

/**
 * DTO representing user preferences.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferencesDto {
    
    private String theme;
    private String language;
    private NotificationSettingsDto notifications;
}