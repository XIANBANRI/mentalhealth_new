package com.sl.mentalhealth.service.impl;

import com.sl.mentalhealth.common.JwtUtil;
import com.sl.mentalhealth.config.LoginUser;
import com.sl.mentalhealth.constant.RedisKeyConstant;
import com.sl.mentalhealth.service.TokenRedisService;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
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

  private String buildTokenKey(String token) {
    return RedisKeyConstant.LOGIN_TOKEN_PREFIX + token;
  }
}