package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.dto.TeacherAppointmentQueryRequest;
import com.sl.mentalhealth.dto.TeacherAppointmentUpdateStatusRequest;
import com.sl.mentalhealth.dto.TeacherAssessmentRecordQueryRequest;
import lombok.Data;

@Data
public class TeacherAppointmentRequestMessage {
  private String requestId;

  private String action;
  private TeacherAppointmentQueryRequest queryRequest;
  private TeacherAppointmentUpdateStatusRequest updateStatusRequest;
  private TeacherAssessmentRecordQueryRequest assessmentRecordQueryRequest;
}