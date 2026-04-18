package com.sl.mentalhealth.vo;

public class AssessmentOptionVO {

  private Long id;
  private Integer optionNo;
  private String optionText;
  private Integer optionScore;

  public AssessmentOptionVO() {
  }

  public AssessmentOptionVO(Long id, Integer optionNo, String optionText, Integer optionScore) {
    this.id = id;
    this.optionNo = optionNo;
    this.optionText = optionText;
    this.optionScore = optionScore;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getOptionNo() {
    return optionNo;
  }

  public void setOptionNo(Integer optionNo) {
    this.optionNo = optionNo;
  }

  public String getOptionText() {
    return optionText;
  }

  public void setOptionText(String optionText) {
    this.optionText = optionText;
  }

  public Integer getOptionScore() {
    return optionScore;
  }

  public void setOptionScore(Integer optionScore) {
    this.optionScore = optionScore;
  }
}
