package com.sl.mentalhealth.service;

import com.sl.mentalhealth.dto.TeacherAppointmentQueryRequest;
import com.sl.mentalhealth.dto.TeacherAppointmentUpdateStatusRequest;
import com.sl.mentalhealth.dto.TeacherAssessmentRecordQueryRequest;
import com.sl.mentalhealth.kafka.TeacherAppointmentRequestProducer;
import com.sl.mentalhealth.kafka.message.TeacherAppointmentResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherAppointmentGatewayService {

  private final TeacherAppointmentRequestProducer teacherAppointmentRequestProducer;
  private final PendingTeacherAppointmentService pendingTeacherAppointmentService;

  public TeacherAppointmentResponseMessage query(TeacherAppointmentQueryRequest request) {
    String requestId = teacherAppointmentRequestProducer.sendQuery(request);
    return pendingTeacherAppointmentService.waitResponse(requestId, 8);
  }

  public TeacherAppointmentResponseMessage record(TeacherAppointmentQueryRequest request) {
    String requestId = teacherAppointmentRequestProducer.sendRecord(request);
    return pendingTeacherAppointmentService.waitResponse(requestId, 8);
  }

  public TeacherAppointmentResponseMessage updateStatus(TeacherAppointmentUpdateStatusRequest request) {
    String requestId = teacherAppointmentRequestProducer.sendUpdateStatus(request);
    return pendingTeacherAppointmentService.waitResponse(requestId, 8);
  }

  public TeacherAppointmentResponseMessage assessmentRecord(TeacherAssessmentRecordQueryRequest request) {
    String requestId = teacherAppointmentRequestProducer.sendAssessmentRecord(request);
    return pendingTeacherAppointmentService.waitResponse(requestId, 8);
  }
}