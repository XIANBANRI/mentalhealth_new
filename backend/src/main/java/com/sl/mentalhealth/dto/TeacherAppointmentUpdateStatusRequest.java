package com.sl.mentalhealth.dto;

import lombok.Data;

@Data
public class TeacherAppointmentUpdateStatusRequest {
  private Long id;
  private String teacherAccount;
  private String status;
  private String offlineRecord;
  private String rejectReason;
}