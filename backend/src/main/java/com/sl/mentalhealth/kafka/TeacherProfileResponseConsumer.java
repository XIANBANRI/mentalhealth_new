package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.TeacherProfileResponseMessage;
import com.sl.mentalhealth.service.PendingTeacherProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherProfileResponseConsumer {

  private final PendingTeacherProfileService pendingTeacherProfileService;

  @KafkaListener(
      topics = KafkaTopics.TEACHER_PROFILE_RESPONSE,
      groupId = "mental-health-teacher-profile-response-group",
      containerFactory = "teacherProfileResponseKafkaListenerContainerFactory"
  )
  public void consume(TeacherProfileResponseMessage message) {
    pendingTeacherProfileService.complete(message.getRequestId(), message);
  }
}