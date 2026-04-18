package com.sl.mentalhealth.vo;

public class AssessmentScaleManageVO {

  private Long scaleId;
  private String scaleCode;
  private String scaleName;
  private String scaleType;
  private Integer questionCount;
  private Integer scoreMin;
  private Integer scoreMax;
  private Integer status;
  private Integer deletedFlag;
  private Integer currentVersionNo;

  public Long getScaleId() {
    return scaleId;
  }

  public void setScaleId(Long scaleId) {
    this.scaleId = scaleId;
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

  public Integer getQuestionCount() {
    return questionCount;
  }

  public void setQuestionCount(Integer questionCount) {
    this.questionCount = questionCount;
  }

  public Integer getScoreMin() {
    return scoreMin;
  }

  public void setScoreMin(Integer scoreMin) {
    this.scoreMin = scoreMin;
  }

  public Integer getScoreMax() {
    return scoreMax;
  }

  public void setScoreMax(Integer scoreMax) {
    this.scoreMax = scoreMax;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getDeletedFlag() {
    return deletedFlag;
  }

  public void setDeletedFlag(Integer deletedFlag) {
    this.deletedFlag = deletedFlag;
  }

  public Integer getCurrentVersionNo() {
    return currentVersionNo;
  }

  public void setCurrentVersionNo(Integer currentVersionNo) {
    this.currentVersionNo = currentVersionNo;
  }
}