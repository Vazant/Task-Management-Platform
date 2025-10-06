package com.taskboard.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

  @NotBlank(message = "{validation.username.required}")
  @Size(min = 3, max = 20, message = "{validation.username.size}")
  private String username;

  @NotBlank(message = "{validation.email.required}")
  @Email(message = "{validation.email.invalid}")
  private String email;

  @NotBlank(message = "{validation.password.required}")
  @Size(min = 8, message = "{validation.password.size}")
  private String password;

  @NotBlank(message = "{validation.password.confirm.required}")
  private String confirmPassword;
}
