package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorProfileResponseMessage;
import com.sl.mentalhealth.service.PendingCounselorProfileService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CounselorProfileResponseConsumer {

  private final PendingCounselorProfileService pendingCounselorProfileService;

  public CounselorProfileResponseConsumer(PendingCounselorProfileService pendingCounselorProfileService) {
    this.pendingCounselorProfileService = pendingCounselorProfileService;
  }

  @KafkaListener(
      topics = KafkaTopics.COUNSELOR_PROFILE_RESPONSE,
      groupId = "mental-health-counselor-profile-response",
      containerFactory = "counselorProfileResponseKafkaListenerContainerFactory"
  )
  public void consume(CounselorProfileResponseMessage response) {
    if (response.isSuccess()) {
      pendingCounselorProfileService.complete(response.getCorrelationId(), response.getData());
    } else {
      pendingCounselorProfileService.completeExceptionally(
          response.getCorrelationId(),
          new RuntimeException(response.getMessage())
      );
    }
  }
}