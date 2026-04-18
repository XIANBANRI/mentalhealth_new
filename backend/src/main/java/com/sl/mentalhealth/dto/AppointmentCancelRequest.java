package com.sl.mentalhealth.dto;

import lombok.Data;

@Data
public class AppointmentCancelRequest {

  private Long appointmentId;
  private String studentId;
}