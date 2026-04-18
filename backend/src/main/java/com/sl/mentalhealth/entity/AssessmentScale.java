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
@TableName("assessment_scale")
public class AssessmentScale {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  @TableField("scale_code")
  private String scaleCode;

  @TableField("scale_name")
  private String scaleName;

  @TableField("scale_type")
  private String scaleType;

  @TableField("description")
  private String description;

  @TableField("question_count")
  private Integer questionCount;

  @TableField("score_min")
  private Integer scoreMin;

  @TableField("score_max")
  private Integer scoreMax = 0;

  @TableField("status")
  private Integer status = 1;

  @TableField("deleted_flag")
  private Integer deletedFlag = 0;

  @TableField("current_version_id")
  private Long currentVersionId;

  @TableField("created_by")
  private String createdBy;

  @TableField("created_at")
  private LocalDateTime createdAt;

  @TableField("updated_at")
  private LocalDateTime updatedAt;
}