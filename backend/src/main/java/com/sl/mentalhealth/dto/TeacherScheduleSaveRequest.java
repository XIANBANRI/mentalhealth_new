package com.sl.mentalhealth.dto;

import lombok.Data;

@Data
public class TeacherScheduleSaveRequest {
  private Long id;
  private String teacherAccount;
  private Integer weekDay;
  private String startTime;
  private String endTime;
  private Integer maxAppointments;
  private String remark;
}