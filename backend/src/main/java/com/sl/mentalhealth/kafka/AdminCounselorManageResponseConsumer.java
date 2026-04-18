package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminCounselorManageResponseMessage;
import com.sl.mentalhealth.service.PendingAdminCounselorManageService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AdminCounselorManageResponseConsumer {

  private final PendingAdminCounselorManageService pendingService;

  public AdminCounselorManageResponseConsumer(
      PendingAdminCounselorManageService pendingService) {
    this.pendingService = pendingService;
  }

  @KafkaListener(
      topics = KafkaTopics.ADMIN_COUNSELOR_MANAGE_RESPONSE,
      containerFactory = "adminCounselorManageResponseKafkaListenerContainerFactory"
  )
  public void consume(AdminCounselorManageResponseMessage message) {
    pendingService.complete(message);
  }
}