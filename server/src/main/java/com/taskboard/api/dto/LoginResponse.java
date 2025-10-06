package com.taskboard.api.dto;

import com.taskboard.user.model.UserEntity;

public class LoginResponse {
  private UserEntity user;
  private String token;
  private String refreshToken;

  public LoginResponse() {}

  public LoginResponse(UserEntity user, String token, String refreshToken) {
    this.user = user;
    this.token = token;
    this.refreshToken = refreshToken;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
