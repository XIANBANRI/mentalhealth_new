package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorStudentResponseMessage;
import com.sl.mentalhealth.service.PendingCounselorStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounselorStudentResponseConsumer {

  private final PendingCounselorStudentService pendingCounselorStudentService;

  @KafkaListener(
      topics = KafkaTopics.COUNSELOR_STUDENT_RESPONSE,
      groupId = "mh-counselor-student-response-group",
      containerFactory = "counselorStudentResponseKafkaListenerContainerFactory"
  )
  public void consume(CounselorStudentResponseMessage message) {
    pendingCounselorStudentService.complete(message);
  }
}