package com.sl.mentalhealth.service;

import com.sl.mentalhealth.config.LoginUser;

public interface TokenRedisService {

  void saveToken(String token, LoginUser loginUser);

  LoginUser getLoginUser(String token);

  void deleteToken(String token);

  void refreshToken(String token);

  boolean exists(String token);
}