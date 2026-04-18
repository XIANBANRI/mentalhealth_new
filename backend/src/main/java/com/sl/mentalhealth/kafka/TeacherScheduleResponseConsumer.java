package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.TeacherScheduleResponseMessage;
import com.sl.mentalhealth.service.PendingTeacherScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherScheduleResponseConsumer {

  private final PendingTeacherScheduleService pendingTeacherScheduleService;

  @KafkaListener(
      topics = KafkaTopics.TEACHER_SCHEDULE_RESPONSE,
      groupId = "mental-health-teacher-schedule-response-group",
      containerFactory = "teacherScheduleResponseKafkaListenerContainerFactory"
  )
  public void consume(TeacherScheduleResponseMessage message) {
    pendingTeacherScheduleService.complete(message.getRequestId(), message);
  }
}