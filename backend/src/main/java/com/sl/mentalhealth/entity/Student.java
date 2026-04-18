package com.sl.mentalhealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student")
public class Student {

  @TableId(value = "student_id", type = IdType.INPUT)
  private String studentId;

  @TableField("name")
  private String name;

  @TableField("college")
  private String college;

  @TableField("class_name")
  private String className;

  @TableField("password")
  private String password;

  @TableField("phone")
  private String phone;

  @TableField("grade")
  private String grade;

  @TableField("avatar_url")
  private String avatarUrl;

  public Student() {
  }
}