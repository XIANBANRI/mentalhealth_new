package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.dto.TeacherAppointmentQueryRequest;
import com.sl.mentalhealth.dto.TeacherAppointmentUpdateStatusRequest;
import com.sl.mentalhealth.dto.TeacherAssessmentRecordQueryRequest;
import com.sl.mentalhealth.kafka.message.TeacherAppointmentRequestMessage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherAppointmentRequestProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public String sendQuery(TeacherAppointmentQueryRequest request) {
    return send("QUERY", request, null, null);
  }

  public String sendRecord(TeacherAppointmentQueryRequest request) {
    return send("RECORD", request, null, null);
  }

  public String sendUpdateStatus(TeacherAppointmentUpdateStatusRequest request) {
    return send("UPDATE_STATUS", null, request, null);
  }

  public String sendAssessmentRecord(TeacherAssessmentRecordQueryRequest request) {
    return send("ASSESSMENT_RECORD", null, null, request);
  }

  private String send(
      String action,
      TeacherAppointmentQueryRequest queryRequest,
      TeacherAppointmentUpdateStatusRequest updateStatusRequest,
      TeacherAssessmentRecordQueryRequest assessmentRecordQueryRequest
  ) {
    String requestId = UUID.randomUUID().toString();

    TeacherAppointmentRequestMessage message = new TeacherAppointmentRequestMessage();
    message.setRequestId(requestId);
    message.setAction(action);
    message.setQueryRequest(queryRequest);
    message.setUpdateStatusRequest(updateStatusRequest);
    message.setAssessmentRecordQueryRequest(assessmentRecordQueryRequest);

    kafkaTemplate.send(KafkaTopics.TEACHER_APPOINTMENT_REQUEST, requestId, message);
    return requestId;
  }
}