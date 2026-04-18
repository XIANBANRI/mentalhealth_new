package com.sl.mentalhealth.vo;

public class AssessmentScaleVO {

  private Long id;
  private String scaleCode;
  private String scaleName;
  private String scaleType;
  private String description;
  private Integer questionCount;
  private Long currentVersionId;
  private Integer currentVersionNo;

  public AssessmentScaleVO() {
  }

  public AssessmentScaleVO(Long id, String scaleCode, String scaleName,
      String scaleType, String description, Integer questionCount,
      Long currentVersionId, Integer currentVersionNo) {
    this.id = id;
    this.scaleCode = scaleCode;
    this.scaleName = scaleName;
    this.scaleType = scaleType;
    this.description = description;
    this.questionCount = questionCount;
    this.currentVersionId = currentVersionId;
    this.currentVersionNo = currentVersionNo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getScaleCode() {
    return scaleCode;
  }

  public void setScaleCode(String scaleCode) {
    this.scaleCode = scaleCode;
  }

  public String getScaleName() {
    return scaleName;
  }

  public void setScaleName(String scaleName) {
    this.scaleName = scaleName;
  }

  public String getScaleType() {
    return scaleType;
  }

  public void setScaleType(String scaleType) {
    this.scaleType = scaleType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getQuestionCount() {
    return questionCount;
  }

  public void setQuestionCount(Integer questionCount) {
    this.questionCount = questionCount;
  }

  public Long getCurrentVersionId() {
    return currentVersionId;
  }

  public void setCurrentVersionId(Long currentVersionId) {
    this.currentVersionId = currentVersionId;
  }

  public Integer getCurrentVersionNo() {
    return currentVersionNo;
  }

  public void setCurrentVersionNo(Integer currentVersionNo) {
    this.currentVersionNo = currentVersionNo;
  }
}
