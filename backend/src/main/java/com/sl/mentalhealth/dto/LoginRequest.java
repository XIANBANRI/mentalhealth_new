package com.sl.mentalhealth.dto;

import lombok.Data;

@Data
public class LoginRequest {

  private String role;
  private String username;
  private String password;

  public LoginRequest() {
  }

}