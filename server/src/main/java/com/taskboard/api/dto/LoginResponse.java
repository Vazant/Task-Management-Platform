package com.taskboard.api.dto;

import com.taskboard.api.model.User;

public class LoginResponse {
  private User user;
  private String token;
  private String refreshToken;

  public LoginResponse() {}

  public LoginResponse(User user, String token, String refreshToken) {
    this.user = user;
    this.token = token;
    this.refreshToken = refreshToken;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
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
