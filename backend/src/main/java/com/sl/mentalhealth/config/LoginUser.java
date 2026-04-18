package com.sl.mentalhealth.config;

import java.io.Serializable;

public class LoginUser implements Serializable {

  private String username;
  private String role;
  private String token;
  private Long loginTime;

  public LoginUser() {
  }

  public LoginUser(String username, String role) {
    this.username = username;
    this.role = role;
  }

  public LoginUser(String username, String role, String token, Long loginTime) {
    this.username = username;
    this.role = role;
    this.token = token;
    this.loginTime = loginTime;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Long getLoginTime() {
    return loginTime;
  }

  public void setLoginTime(Long loginTime) {
    this.loginTime = loginTime;
  }
}