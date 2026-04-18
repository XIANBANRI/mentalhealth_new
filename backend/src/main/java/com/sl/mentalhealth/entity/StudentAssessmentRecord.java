package com.sl.mentalhealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("student_assessment_record")
public class StudentAssessmentRecord {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  @TableField("student_id")
  private String studentId;

  @TableField("semester")
  private String semester;

  @TableField("scale_id")
  private Long scaleId;

  @TableField("scale_version_id")
  private Long scaleVersionId;

  @TableField("scale_code")
  private String scaleCode;

  @TableField("scale_name")
  private String scaleName;

  @TableField("raw_score")
  private Integer rawScore;

  @TableField("result_level")
  private String resultLevel;

  @TableField("result_summary")
  private String resultSummary;

  @TableField("suggestion")
  private String suggestion;

  @TableField("status")
  private String status;

  @TableField("submitted_at")
  private LocalDateTime submittedAt;

  @TableField("created_at")
  private LocalDateTime createdAt;

  @TableField("updated_at")
  private LocalDateTime updatedAt;
}