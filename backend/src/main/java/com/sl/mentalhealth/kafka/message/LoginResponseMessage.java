package com.sl.mentalhealth.kafka.message;

public class LoginResponseMessage {

  private String requestId;
  private Boolean success;
  private String message;
  private String role;
  private String username;
  private String redirectPath;

  public LoginResponseMessage() {
  }

  public LoginResponseMessage(String requestId, Boolean success, String message,
      String role, String username, String redirectPath) {
    this.requestId = requestId;
    this.success = success;
    this.message = message;
    this.role = role;
    this.username = username;
    this.redirectPath = redirectPath;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
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

  public String getRedirectPath() {
    return redirectPath;
  }

  public void setRedirectPath(String redirectPath) {
    this.redirectPath = redirectPath;
  }
}