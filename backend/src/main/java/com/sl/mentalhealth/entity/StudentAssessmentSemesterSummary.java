package com.sl.mentalhealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("student_assessment_semester_summary")
public class StudentAssessmentSemesterSummary {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  @TableField("student_id")
  private String studentId;

  @TableField("semester")
  private String semester;

  @TableField("tested_count")
  private Integer testedCount;

  @TableField("score_summary")
  private String scoreSummary;

  @TableField("semester_level")
  private String semesterLevel;

  @TableField("last_tested_at")
  private LocalDateTime lastTestedAt;

  @TableField("created_at")
  private LocalDateTime createdAt;

  @TableField("updated_at")
  private LocalDateTime updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
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

  public LocalDateTime getLastTestedAt() {
    return lastTestedAt;
  }

  public void setLastTestedAt(LocalDateTime lastTestedAt) {
    this.lastTestedAt = lastTestedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}