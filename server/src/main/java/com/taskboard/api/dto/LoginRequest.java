package com.taskboard.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  @NotBlank(message = "{validation.email.required}")
  @Email(message = "{validation.email.invalid}")
  private String email;

  @NotBlank(message = "{validation.password.required}")
  private String password;
}
