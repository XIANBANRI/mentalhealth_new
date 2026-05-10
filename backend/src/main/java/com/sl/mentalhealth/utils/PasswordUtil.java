package com.sl.mentalhealth.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

  private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

  private PasswordUtil() {
  }

  public static String encode(String rawPassword) {
    if (rawPassword == null) {
      return null;
    }
    return ENCODER.encode(rawPassword);
  }

  public static boolean matches(String rawPassword, String dbPassword) {
    if (rawPassword == null || dbPassword == null) {
      return false;
    }

    if (isEncoded(dbPassword)) {
      return ENCODER.matches(rawPassword, dbPassword);
    }

    // 兼容数据库中原来保存的明文密码
    return rawPassword.equals(dbPassword);
  }

  public static boolean isEncoded(String password) {
    if (password == null) {
      return false;
    }
    return password.startsWith("$2a$")
        || password.startsWith("$2b$")
        || password.startsWith("$2y$");
  }
}