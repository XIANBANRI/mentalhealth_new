package com.sl.mentalhealth.service;

import com.sl.mentalhealth.config.LoginUser;
import java.util.List;
import java.util.Map;

public interface TokenRedisService {

  void saveToken(String token, LoginUser loginUser);

  LoginUser getLoginUser(String token);

  void deleteToken(String token);

  void refreshToken(String token);

  boolean exists(String token);

  /**
   * 当前在线用户数。
   * 统计 Redis 中仍然存在的 login:token:* 数量。
   * 如果同一个账号在多个设备登录，会按多个在线会话统计。
   */
  long countOnlineUsers();

  /**
   * 按角色统计当前在线人数。
   */
  Map<String, Long> countOnlineUsersByRole();

  /**
   * 当前在线用户列表。
   */
  List<LoginUser> listOnlineUsers();
}