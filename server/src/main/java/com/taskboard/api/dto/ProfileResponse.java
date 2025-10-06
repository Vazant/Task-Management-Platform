package com.taskboard.api.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
  private Long id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private String avatar;
  private String role;
  private LocalDateTime lastLogin;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
