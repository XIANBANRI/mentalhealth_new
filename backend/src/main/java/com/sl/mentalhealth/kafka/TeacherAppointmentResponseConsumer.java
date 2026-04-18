package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.TeacherAppointmentResponseMessage;
import com.sl.mentalhealth.service.PendingTeacherAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherAppointmentResponseConsumer {

  private final PendingTeacherAppointmentService pendingTeacherAppointmentService;

  @KafkaListener(
      topics = KafkaTopics.TEACHER_APPOINTMENT_RESPONSE,
      groupId = "mental-health-teacher-appointment-response-group",
      containerFactory = "teacherAppointmentResponseKafkaListenerContainerFactory"
  )
  public void consume(TeacherAppointmentResponseMessage message) {
    pendingTeacherAppointmentService.complete(message.getRequestId(), message);
  }
}