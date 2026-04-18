package com.sl.mentalhealth.dto;

import lombok.Data;

@Data
public class TeacherAppointmentAuditRequest {

  private Long appointmentId;
  private String teacherAccount;
  private String teacherReply;
}