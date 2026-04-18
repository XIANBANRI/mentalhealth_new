package com.sl.mentalhealth.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdminProfileResponseVO {

  /**
   * 管理员账号：admin.account
   */
  private String account;

  /**
   * 管理员姓名：admin.name
   */
  private String name;

  /**
   * 管理员头像地址：admin.avatar_url
   */
  private String avatarUrl;
}