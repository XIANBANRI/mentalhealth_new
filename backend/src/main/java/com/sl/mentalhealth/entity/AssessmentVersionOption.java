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
@TableName("assessment_version_option")
public class AssessmentVersionOption {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  @TableField("version_question_id")
  private Long versionQuestionId;

  @TableField("option_no")
  private Integer optionNo;

  @TableField("option_text")
  private String optionText;

  @TableField("option_score")
  private Integer optionScore;

  @TableField("created_at")
  private LocalDateTime createdAt;
}