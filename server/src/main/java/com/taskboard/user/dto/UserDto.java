package com.taskboard.user.dto;

import com.taskboard.user.model.UserRole;
import java.util.Date;
import lombok.*;

/** DTO representing user data for API responses. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

  private String id;
  private String email;
  private String username;
  private UserRole role;
  private String avatar;
  private Date createdAt;
  private Date lastLogin;
  private UserPreferencesDto preferences;
}
