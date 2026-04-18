package com.sl.mentalhealth.config;

public class UserContext {

  private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

  private UserContext() {
  }

  public static void set(LoginUser loginUser) {
    HOLDER.set(loginUser);
  }

  public static void setLoginUser(LoginUser loginUser) {
    HOLDER.set(loginUser);
  }

  public static LoginUser get() {
    return HOLDER.get();
  }

  public static LoginUser getLoginUser() {
    return HOLDER.get();
  }

  public static String getUsername() {
    LoginUser loginUser = HOLDER.get();
    return loginUser == null ? null : loginUser.getUsername();
  }

  public static String getRole() {
    LoginUser loginUser = HOLDER.get();
    return loginUser == null ? null : loginUser.getRole();
  }

  public static void clear() {
    HOLDER.remove();
  }
}