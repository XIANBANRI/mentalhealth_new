package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AppointmentResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentResponseProducer {

  private final KafkaTemplate<String, AppointmentResponseMessage> kafkaTemplate;

  public void send(AppointmentResponseMessage message) {
    kafkaTemplate.send(KafkaTopics.APPOINTMENT_RESPONSE, message.getRequestId(), message);
  }
}