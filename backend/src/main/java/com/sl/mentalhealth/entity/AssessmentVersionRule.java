package com.sl.mentalhealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("assessment_version_rule")
public class AssessmentVersionRule {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  @TableField("version_id")
  private Long versionId;

  @TableField("min_score")
  private Integer minScore;

  @TableField("max_score")
  private Integer maxScore;

  @TableField("result_level")
  private String resultLevel;

  @TableField("result_summary")
  private String resultSummary;

  @TableField("suggestion")
  private String suggestion;

  @TableField("created_at")
  private LocalDateTime createdAt;
}