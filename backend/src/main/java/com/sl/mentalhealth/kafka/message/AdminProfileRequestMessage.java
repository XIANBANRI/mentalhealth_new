package com.sl.mentalhealth.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminProfileRequestMessage {

  public static final String ACTION_QUERY_PROFILE = "QUERY_PROFILE";
  public static final String ACTION_UPDATE_AVATAR = "UPDATE_AVATAR";

  /**
   * 请求唯一标识
   */
  private String requestId;

  /**
   * 操作类型
   */
  private String action;

  /**
   * 管理员账号
   */
  private String account;

  /**
   * 头像地址
   */
  private String avatarUrl;
}