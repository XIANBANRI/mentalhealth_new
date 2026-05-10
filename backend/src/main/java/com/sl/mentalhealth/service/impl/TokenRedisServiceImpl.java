package com.sl.mentalhealth.service.impl;

import com.sl.mentalhealth.common.JwtUtil;
import com.sl.mentalhealth.config.LoginUser;
import com.sl.mentalhealth.constant.RedisKeyConstant;
import com.sl.mentalhealth.service.TokenRedisService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

@Service
public class TokenRedisServiceImpl implements TokenRedisService {

  private static final long TOKEN_EXPIRE_MINUTES = 24 * 60L;

  private final RedisTemplate<String, Object> redisTemplate;

  public TokenRedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public void saveToken(String token, LoginUser loginUser) {
    String key = buildTokenKey(token);
    redisTemplate.opsForValue().set(key, loginUser, TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
  }

  @Override
  public LoginUser getLoginUser(String token) {
    String key = buildTokenKey(token);
    Object value = redisTemplate.opsForValue().get(key);
    return convertToLoginUser(value);
  }

  @Override
  public void deleteToken(String token) {
    redisTemplate.delete(buildTokenKey(token));
  }

  @Override
  public void refreshToken(String token) {
    String key = buildTokenKey(token);
    Boolean exists = redisTemplate.hasKey(key);
    if (Boolean.TRUE.equals(exists) && JwtUtil.validateToken(token)) {
      redisTemplate.expire(key, TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }
  }

  @Override
  public boolean exists(String token) {
    Boolean exists = redisTemplate.hasKey(buildTokenKey(token));
    return Boolean.TRUE.equals(exists);
  }

  /**
   * 当前在线用户数。
   *
   * 这里统计的是 Redis 中仍然存在的 login:token:* 数量。
   * 如果同一个用户在多个浏览器或设备登录，会被统计为多个在线会话。
   */
  @Override
  public long countOnlineUsers() {
    return scanTokenKeys().size();
  }

  /**
   * 按角色统计在线人数。
   *
   * 返回示例：
   * {
   *   "student": 10,
   *   "teacher": 2,
   *   "counselor": 1,
   *   "admin": 1
   * }
   */
  @Override
  public Map<String, Long> countOnlineUsersByRole() {
    Map<String, Long> result = new LinkedHashMap<>();
    result.put("student", 0L);
    result.put("teacher", 0L);
    result.put("counselor", 0L);
    result.put("admin", 0L);
    result.put("unknown", 0L);

    List<String> keys = scanTokenKeys();
    for (String key : keys) {
      Object value = redisTemplate.opsForValue().get(key);
      LoginUser loginUser = convertToLoginUser(value);

      String role = "unknown";
      if (loginUser != null && loginUser.getRole() != null && !loginUser.getRole().isBlank()) {
        role = loginUser.getRole().trim();
      }

      result.put(role, result.getOrDefault(role, 0L) + 1L);
    }

    return result;
  }

  /**
   * 当前在线用户列表。
   *
   * 这个方法主要方便管理员后台展示。
   * 如果暂时没有页面，也可以先不接 Controller。
   */
  @Override
  public List<LoginUser> listOnlineUsers() {
    List<LoginUser> result = new ArrayList<>();

    List<String> keys = scanTokenKeys();
    for (String key : keys) {
      Object value = redisTemplate.opsForValue().get(key);
      LoginUser loginUser = convertToLoginUser(value);

      if (loginUser != null) {
        result.add(loginUser);
      }
    }

    return result;
  }

  private List<String> scanTokenKeys() {
    List<String> keys = new ArrayList<>();
    String pattern = RedisKeyConstant.LOGIN_TOKEN_PREFIX + "*";

    ScanOptions options = ScanOptions.scanOptions()
        .match(pattern)
        .count(500)
        .build();

    try (Cursor<String> cursor = redisTemplate.scan(options)) {
      while (cursor.hasNext()) {
        keys.add(cursor.next());
      }
    }

    return keys;
  }

  private LoginUser convertToLoginUser(Object value) {
    if (value == null) {
      return null;
    }

    if (value instanceof LoginUser loginUser) {
      return loginUser;
    }

    if (value instanceof Map<?, ?> map) {
      LoginUser loginUser = new LoginUser();

      Object username = map.get("username");
      if (username != null) {
        loginUser.setUsername(String.valueOf(username));
      }

      Object role = map.get("role");
      if (role != null) {
        loginUser.setRole(String.valueOf(role));
      }

      Object tokenValue = map.get("token");
      if (tokenValue != null) {
        loginUser.setToken(String.valueOf(tokenValue));
      }

      Object loginTime = map.get("loginTime");
      if (loginTime != null) {
        try {
          loginUser.setLoginTime(Long.parseLong(String.valueOf(loginTime)));
        } catch (NumberFormatException ignored) {
        }
      }

      return loginUser;
    }

    return null;
  }

  private String buildTokenKey(String token) {
    return RedisKeyConstant.LOGIN_TOKEN_PREFIX + token;
  }
}