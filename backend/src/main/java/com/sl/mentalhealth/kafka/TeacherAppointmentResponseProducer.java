package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.TeacherAppointmentResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherAppointmentResponseProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void send(TeacherAppointmentResponseMessage message) {
    kafkaTemplate.send(KafkaTopics.TEACHER_APPOINTMENT_RESPONSE, message.getRequestId(), message);
  }
}