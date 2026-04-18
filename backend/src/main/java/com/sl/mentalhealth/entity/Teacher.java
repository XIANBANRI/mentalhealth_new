package com.sl.mentalhealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("teacher")
public class Teacher {

  @TableId(value = "account", type = IdType.INPUT)
  private String account;

  @TableField("password")
  private String password;

  @TableField("teacher_name")
  private String teacherName;

  @TableField("office_location")
  private String officeLocation;

  @TableField("phone")
  private String phone;

  @TableField("avatar_url")
  private String avatarUrl;
}