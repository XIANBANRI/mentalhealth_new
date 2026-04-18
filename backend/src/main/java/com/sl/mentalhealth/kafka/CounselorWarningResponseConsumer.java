package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorWarningResponseMessage;
import com.sl.mentalhealth.service.PendingCounselorWarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounselorWarningResponseConsumer {

  private final PendingCounselorWarningService pendingCounselorWarningService;

  @KafkaListener(
      topics = KafkaTopics.COUNSELOR_WARNING_RESPONSE,
      groupId = "mh-counselor-warning-response-group",
      containerFactory = "counselorWarningResponseKafkaListenerContainerFactory"
  )
  public void consume(CounselorWarningResponseMessage message) {
    pendingCounselorWarningService.complete(message);
  }
}