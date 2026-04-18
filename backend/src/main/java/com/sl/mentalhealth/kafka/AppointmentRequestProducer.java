package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AppointmentRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentRequestProducer {

  private final KafkaTemplate<String, AppointmentRequestMessage> kafkaTemplate;

  public void send(AppointmentRequestMessage message) {
    kafkaTemplate.send(KafkaTopics.APPOINTMENT_REQUEST, message.getRequestId(), message);
  }
}