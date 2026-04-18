package com.sl.mentalhealth.mapper.result;

public class SemesterDangerCountResult {

  private String semester;
  private Long dangerCount;

  public SemesterDangerCountResult() {
  }

  public SemesterDangerCountResult(String semester, Long dangerCount) {
    this.semester = semester;
    this.dangerCount = dangerCount;
  }

  public String getSemester() {
    return semester;
  }

  public void setSemester(String semester) {
    this.semester = semester;
  }

  public Long getDangerCount() {
    return dangerCount;
  }

  public void setDangerCount(Long dangerCount) {
    this.dangerCount = dangerCount;
  }
}