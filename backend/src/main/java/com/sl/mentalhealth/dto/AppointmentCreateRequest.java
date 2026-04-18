package com.sl.mentalhealth.dto;

import lombok.Data;

@Data
public class AppointmentCreateRequest {

  private String studentId;
  private Long scheduleId;
  private String appointmentDate;
  private String purpose;
  private String remark;
}