package com.taskboard.user.dto;

import lombok.*;

/** DTO representing user preferences. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferencesDto {

  private String theme;
  private String language;
  private NotificationSettingsDto notifications;
}
