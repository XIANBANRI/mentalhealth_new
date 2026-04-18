package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorStudentRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounselorStudentRequestProducer {

  @Qualifier("counselorStudentRequestKafkaTemplate")
  private final KafkaTemplate<String, CounselorStudentRequestMessage> kafkaTemplate;

  public void send(CounselorStudentRequestMessage message) {
    kafkaTemplate.send(KafkaTopics.COUNSELOR_STUDENT_REQUEST, message.getRequestId(), message);
  }
}