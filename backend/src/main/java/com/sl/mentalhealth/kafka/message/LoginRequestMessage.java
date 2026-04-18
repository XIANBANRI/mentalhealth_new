package com.sl.mentalhealth.kafka.message;

public class LoginRequestMessage {

  private String requestId;
  private String role;
  private String username;
  private String password;
  private String requestTime;

  public LoginRequestMessage() {
  }

  public LoginRequestMessage(String requestId, String role, String username, String password, String requestTime) {
    this.requestId = requestId;
    this.role = role;
    this.username = username;
    this.password = password;
    this.requestTime = requestTime;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRequestTime() {
    return requestTime;
  }

  public void setRequestTime(String requestTime) {
    this.requestTime = requestTime;
  }
}