package com.sl.mentalhealth.kafka.message;

public class ResetPasswordRequestMessage {

  private String requestId;
  private String role;
  private String username;
  private String phone;
  private String newPassword;
  private String requestedAt;

  public ResetPasswordRequestMessage() {
  }

  public ResetPasswordRequestMessage(String requestId, String role, String username,
      String phone, String newPassword, String requestedAt) {
    this.requestId = requestId;
    this.role = role;
    this.username = username;
    this.phone = phone;
    this.newPassword = newPassword;
    this.requestedAt = requestedAt;
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

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getRequestedAt() {
    return requestedAt;
  }

  public void setRequestedAt(String requestedAt) {
    this.requestedAt = requestedAt;
  }
}