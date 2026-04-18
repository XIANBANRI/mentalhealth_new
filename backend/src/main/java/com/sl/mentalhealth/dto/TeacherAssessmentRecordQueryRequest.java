package com.sl.mentalhealth.dto;

import lombok.Data;

@Data
public class TeacherAssessmentRecordQueryRequest {
  private String teacherAccount;
  private String studentId;
}