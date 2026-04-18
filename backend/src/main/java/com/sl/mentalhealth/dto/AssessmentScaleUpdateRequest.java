package com.sl.mentalhealth.dto;

import java.util.List;
import lombok.Data;

@Data
public class AssessmentScaleUpdateRequest {

  private Long scaleId;
  private String scaleName;
  private String scaleType;
  private String description;
  private String versionRemark;
  private String operator;
  private List<QuestionDTO> questions;
  private List<RuleDTO> rules;

  @Data
  public static class QuestionDTO {
    private Integer questionNo;
    private String questionText;
    private List<OptionDTO> options;

  }

  @Data
  public static class OptionDTO {
    private Integer optionNo;
    private String optionText;
    private Integer optionScore;

  }

  @Data
  public static class RuleDTO {
    private Integer minScore;
    private Integer maxScore;
    private String resultLevel;
    private String resultSummary;
    private String suggestion;

  }

}