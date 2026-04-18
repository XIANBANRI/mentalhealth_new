package com.sl.mentalhealth.vo;

import java.util.List;

public class AssessmentRecordVO {

  private Long summaryId;
  private String semester;
  private Integer testedCount;
  private String scoreSummary;
  private String semesterLevel;
  private String lastTestedAt;
  private List<AssessmentRecordDetailVO> details;

  public Long getSummaryId() {
    return summaryId;
  }

  public void setSummaryId(Long summaryId) {
    this.summaryId = summaryId;
  }

  public String getSemester() {
    return semester;
  }

  public void setSemester(String semester) {
    this.semester = semester;
  }

  public Integer getTestedCount() {
    return testedCount;
  }

  public void setTestedCount(Integer testedCount) {
    this.testedCount = testedCount;
  }

  public String getScoreSummary() {
    return scoreSummary;
  }

  public void setScoreSummary(String scoreSummary) {
    this.scoreSummary = scoreSummary;
  }

  public String getSemesterLevel() {
    return semesterLevel;
  }

  public void setSemesterLevel(String semesterLevel) {
    this.semesterLevel = semesterLevel;
  }

  public String getLastTestedAt() {
    return lastTestedAt;
  }

  public void setLastTestedAt(String lastTestedAt) {
    this.lastTestedAt = lastTestedAt;
  }

  public List<AssessmentRecordDetailVO> getDetails() {
    return details;
  }

  public void setDetails(List<AssessmentRecordDetailVO> details) {
    this.details = details;
  }
}