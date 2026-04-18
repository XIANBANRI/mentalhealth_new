package com.sl.mentalhealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("student_assessment_answer")
public class StudentAssessmentAnswer {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  @TableField("record_id")
  private Long recordId;

  @TableField("version_question_id")
  private Long versionQuestionId;

  @TableField("question_no")
  private Integer questionNo;

  @TableField("question_text")
  private String questionText;

  @TableField("version_option_id")
  private Long versionOptionId;

  @TableField("option_no")
  private Integer optionNo;

  @TableField("option_text")
  private String optionText;

  @TableField("answer_score")
  private Integer answerScore;

  @TableField("created_at")
  private LocalDateTime createdAt;
}