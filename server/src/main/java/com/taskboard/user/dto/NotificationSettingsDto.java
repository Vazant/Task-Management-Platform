package com.taskboard.user.dto;

import lombok.*;

/** DTO representing notification settings. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettingsDto {

  private boolean email;
  private boolean push;
  private boolean taskUpdates;
  private boolean projectUpdates;
}
