package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorStudentResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounselorStudentResponseProducer {

  @Qualifier("counselorStudentResponseKafkaTemplate")
  private final KafkaTemplate<String, CounselorStudentResponseMessage> kafkaTemplate;

  public void send(CounselorStudentResponseMessage message) {
    kafkaTemplate.send(KafkaTopics.COUNSELOR_STUDENT_RESPONSE, message.getRequestId(), message);
  }
}