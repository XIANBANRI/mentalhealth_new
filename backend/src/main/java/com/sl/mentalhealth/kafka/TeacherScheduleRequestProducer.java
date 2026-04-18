package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.dto.TeacherScheduleDeleteRequest;
import com.sl.mentalhealth.dto.TeacherScheduleQueryRequest;
import com.sl.mentalhealth.dto.TeacherScheduleSaveRequest;
import com.sl.mentalhealth.kafka.message.TeacherScheduleRequestMessage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherScheduleRequestProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public String sendQuery(TeacherScheduleQueryRequest request) {
    return send("QUERY", request, null, null);
  }

  public String sendAdd(TeacherScheduleSaveRequest request) {
    return send("ADD", null, request, null);
  }

  public String sendUpdate(TeacherScheduleSaveRequest request) {
    return send("UPDATE", null, request, null);
  }

  public String sendDelete(TeacherScheduleDeleteRequest request) {
    return send("DELETE", null, null, request);
  }

  private String send(
      String action,
      TeacherScheduleQueryRequest queryRequest,
      TeacherScheduleSaveRequest saveRequest,
      TeacherScheduleDeleteRequest deleteRequest
  ) {
    String requestId = UUID.randomUUID().toString();

    TeacherScheduleRequestMessage message = new TeacherScheduleRequestMessage();
    message.setRequestId(requestId);
    message.setAction(action);
    message.setQueryRequest(queryRequest);
    message.setSaveRequest(saveRequest);
    message.setDeleteRequest(deleteRequest);

    kafkaTemplate.send(KafkaTopics.TEACHER_SCHEDULE_REQUEST, requestId, message);
    return requestId;
  }
}