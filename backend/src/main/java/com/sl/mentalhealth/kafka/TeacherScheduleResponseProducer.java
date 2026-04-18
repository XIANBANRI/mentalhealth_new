package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.TeacherScheduleResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherScheduleResponseProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void send(TeacherScheduleResponseMessage message) {
    kafkaTemplate.send(KafkaTopics.TEACHER_SCHEDULE_RESPONSE, message.getRequestId(), message);
  }
}