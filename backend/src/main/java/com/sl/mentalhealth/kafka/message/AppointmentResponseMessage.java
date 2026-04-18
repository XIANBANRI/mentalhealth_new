package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.vo.AppointmentVO;
import com.sl.mentalhealth.vo.AvailableAppointmentVO;
import lombok.Data;

import java.util.List;

@Data
public class AppointmentResponseMessage {

  private String operation;
  private String requestId;

  private boolean success;
  private String message;

  private Long appointmentId;

  private List<AvailableAppointmentVO> availableTeachers;
  private List<AppointmentVO> appointmentList;
}