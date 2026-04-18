package com.sl.mentalhealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("counselor")
public class Counselor {

  @TableId(value = "account", type = IdType.INPUT)
  private String account;

  @TableField("name")
  private String name;

  @TableField("password")
  private String password;

  @TableField("college")
  private String college;

  @TableField("grade")
  private String grade;

  @TableField("phone")
  private String phone;

  @TableField("avatar_url")
  private String avatarUrl;

  public Counselor() {
  }
}