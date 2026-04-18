package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AppointmentResponseMessage;
import com.sl.mentalhealth.service.PendingAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentResponseConsumer {

  private final PendingAppointmentService pendingAppointmentService;

  @KafkaListener(
      topics = KafkaTopics.APPOINTMENT_RESPONSE,
      groupId = "appointment-response-group",
      containerFactory = "appointmentResponseKafkaListenerContainerFactory"
  )
  public void consume(AppointmentResponseMessage message) {
    pendingAppointmentService.complete(message);
  }
}