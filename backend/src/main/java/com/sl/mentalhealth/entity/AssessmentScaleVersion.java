package com.sl.mentalhealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("assessment_scale_version")
public class AssessmentScaleVersion {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  @TableField("scale_id")
  private Long scaleId;

  @TableField("version_no")
  private Integer versionNo;

  @TableField("version_status")
  private String versionStatus;

  @TableField("source_question_file_name")
  private String sourceQuestionFileName;

  @TableField("source_rule_file_name")
  private String sourceRuleFileName;

  @TableField("version_remark")
  private String versionRemark;

  @TableField("created_by")
  private String createdBy;

  @TableField("created_at")
  private LocalDateTime createdAt;

  @TableField("updated_at")
  private LocalDateTime updatedAt;
}