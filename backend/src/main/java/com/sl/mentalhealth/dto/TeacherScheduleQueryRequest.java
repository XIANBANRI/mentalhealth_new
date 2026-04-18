package com.sl.mentalhealth.dto;

import lombok.Data;

@Data
public class TeacherScheduleQueryRequest {
  private String teacherAccount;
  private Integer weekDay;
}