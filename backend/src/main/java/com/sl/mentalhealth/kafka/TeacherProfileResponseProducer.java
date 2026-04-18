package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.TeacherProfileResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherProfileResponseProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void send(TeacherProfileResponseMessage message) {
    kafkaTemplate.send(KafkaTopics.TEACHER_PROFILE_RESPONSE, message.getRequestId(), message);
  }
}