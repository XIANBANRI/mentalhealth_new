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
@TableName("assessment_version_question")
public class AssessmentVersionQuestion {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  @TableField("version_id")
  private Long versionId;

  @TableField("question_no")
  private Integer questionNo;

  @TableField("question_text")
  private String questionText;

  @TableField("required_flag")
  private Integer requiredFlag = 1;

  @TableField("created_at")
  private LocalDateTime createdAt;
}