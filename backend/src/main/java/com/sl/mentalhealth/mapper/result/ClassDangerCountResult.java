package com.sl.mentalhealth.mapper.result;

public class ClassDangerCountResult {

  private String className;
  private Long dangerCount;

  public ClassDangerCountResult() {
  }

  public ClassDangerCountResult(String className, Long dangerCount) {
    this.className = className;
    this.dangerCount = dangerCount;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public Long getDangerCount() {
    return dangerCount;
  }

  public void setDangerCount(Long dangerCount) {
    this.dangerCount = dangerCount;
  }
}