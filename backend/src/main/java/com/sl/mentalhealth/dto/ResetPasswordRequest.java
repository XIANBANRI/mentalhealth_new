package com.sl.mentalhealth.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {

  private String role;
  private String username;
  private String phone;
  private String password;

  public ResetPasswordRequest() {
  }

  public ResetPasswordRequest(String role, String username, String phone, String password) {
    this.role = role;
    this.username = username;
    this.phone = phone;
    this.password = password;
  }

}