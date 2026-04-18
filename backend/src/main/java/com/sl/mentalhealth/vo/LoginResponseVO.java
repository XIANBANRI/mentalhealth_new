package com.sl.mentalhealth.vo;

public class LoginResponseVO {

  private String role;
  private String username;
  private String redirectPath;
  private String token;

  public LoginResponseVO() {
  }

  public LoginResponseVO(String role, String username, String redirectPath, String token) {
    this.role = role;
    this.username = username;
    this.redirectPath = redirectPath;
    this.token = token;
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

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}