package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminProfileResponseMessage;
import com.sl.mentalhealth.service.PendingAdminProfileService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AdminProfileResponseConsumer {

  private final PendingAdminProfileService pendingAdminProfileService;

  public AdminProfileResponseConsumer(PendingAdminProfileService pendingAdminProfileService) {
    this.pendingAdminProfileService = pendingAdminProfileService;
  }

  @KafkaListener(
      topics = KafkaTopics.ADMIN_PROFILE_RESPONSE,
      containerFactory = "adminProfileResponseKafkaListenerContainerFactory"
  )
  public void consume(AdminProfileResponseMessage message) {
    if (message == null) {
      return;
    }
    pendingAdminProfileService.complete(message);
  }
}