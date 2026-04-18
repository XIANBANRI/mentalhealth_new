package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.TeacherProfileRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherProfileRequestProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void send(TeacherProfileRequestMessage message) {
    kafkaTemplate.send(KafkaTopics.TEACHER_PROFILE_REQUEST, message.getRequestId(), message);
  }
}