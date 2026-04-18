package com.sl.mentalhealth.vo;

public class AssessmentSubmitResultVO {

  private Long recordId;
  private String semester;

  private Long scaleId;
  private String scaleCode;
  private String scaleName;
  private Long versionId;
  private Integer versionNo;

  private Integer rawScore;
  private String resultLevel;
  private String resultSummary;
  private String suggestion;
  private String submittedAt;

  public Long getRecordId() {
    return recordId;
  }

  public void setRecordId(Long recordId) {
    this.recordId = recordId;
  }

  public String getSemester() {
    return semester;
  }

  public void setSemester(String semester) {
    this.semester = semester;
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

  public Integer getRawScore() {
    return rawScore;
  }

  public void setRawScore(Integer rawScore) {
    this.rawScore = rawScore;
  }

  public String getResultLevel() {
    return resultLevel;
  }

  public void setResultLevel(String resultLevel) {
    this.resultLevel = resultLevel;
  }

  public String getResultSummary() {
    return resultSummary;
  }

  public void setResultSummary(String resultSummary) {
    this.resultSummary = resultSummary;
  }

  public String getSuggestion() {
    return suggestion;
  }

  public void setSuggestion(String suggestion) {
    this.suggestion = suggestion;
  }

  public String getSubmittedAt() {
    return submittedAt;
  }

  public void setSubmittedAt(String submittedAt) {
    this.submittedAt = submittedAt;
  }
}
