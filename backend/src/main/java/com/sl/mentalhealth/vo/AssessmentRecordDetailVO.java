package com.sl.mentalhealth.vo;

public class AssessmentRecordDetailVO {

  private Long recordId;
  private String scaleCode;
  private String scaleName;
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