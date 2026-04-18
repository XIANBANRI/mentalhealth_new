package com.sl.mentalhealth.dto;

import lombok.Data;

@Data
public class TeacherAppointmentQueryRequest {
  private String teacherAccount;
  private String studentId;
  private String appointmentDate;
  private String status;
}