package com.sl.mentalhealth.vo;

import java.util.List;

public class AssessmentScaleDetailVO {

  private Long scaleId;
  private String scaleCode;
  private String scaleName;
  private String description;
  private Long versionId;
  private Integer versionNo;
  private List<AssessmentQuestionVO> questions;

  public AssessmentScaleDetailVO() {
  }

  public AssessmentScaleDetailVO(Long scaleId, String scaleCode, String scaleName,
      String description, Long versionId, Integer versionNo, List<AssessmentQuestionVO> questions) {
    this.scaleId = scaleId;
    this.scaleCode = scaleCode;
    this.scaleName = scaleName;
    this.description = description;
    this.versionId = versionId;
    this.versionNo = versionNo;
    this.questions = questions;
  }

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getVersionId() {
    return versionId;
  }

  public void setVersionId(Long versionId) {
    this.versionId = versionId;
  }

  public Integer getVersionNo() {
    return versionNo;
  }

  public void setVersionNo(Integer versionNo) {
    this.versionNo = versionNo;
  }

  public List<AssessmentQuestionVO> getQuestions() {
    return questions;
  }

  public void setQuestions(List<AssessmentQuestionVO> questions) {
    this.questions = questions;
  }
}