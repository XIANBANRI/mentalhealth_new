package com.sl.mentalhealth.vo;

import java.util.List;

public class AssessmentQuestionVO {

  private Long id;
  private Integer questionNo;
  private String questionText;
  private Integer requiredFlag;
  private List<AssessmentOptionVO> options;

  public AssessmentQuestionVO() {
  }

  public AssessmentQuestionVO(Long id, Integer questionNo, String questionText,
      Integer requiredFlag, List<AssessmentOptionVO> options) {
    this.id = id;
    this.questionNo = questionNo;
    this.questionText = questionText;
    this.requiredFlag = requiredFlag;
    this.options = options;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getQuestionNo() {
    return questionNo;
  }

  public void setQuestionNo(Integer questionNo) {
    this.questionNo = questionNo;
  }

  public String getQuestionText() {
    return questionText;
  }

  public void setQuestionText(String questionText) {
    this.questionText = questionText;
  }

  public Integer getRequiredFlag() {
    return requiredFlag;
  }

  public void setRequiredFlag(Integer requiredFlag) {
    this.requiredFlag = requiredFlag;
  }

  public List<AssessmentOptionVO> getOptions() {
    return options;
  }

  public void setOptions(List<AssessmentOptionVO> options) {
    this.options = options;
  }
}
