package com.sl.mentalhealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("counselor_class_mapping")
public class CounselorClassMapping {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  @TableField("counselor_account")
  private String counselorAccount;

  @TableField("class_name")
  private String className;

  public CounselorClassMapping() {
  }
}