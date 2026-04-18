package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminTeacherManageResponseMessage;
import com.sl.mentalhealth.service.PendingAdminTeacherManageService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AdminTeacherManageResponseConsumer {

  private final PendingAdminTeacherManageService pendingService;

  public AdminTeacherManageResponseConsumer(PendingAdminTeacherManageService pendingService) {
    this.pendingService = pendingService;
  }

  @KafkaListener(
      topics = KafkaTopics.ADMIN_TEACHER_MANAGE_RESPONSE,
      containerFactory = "adminTeacherManageResponseKafkaListenerContainerFactory"
  )
  public void consume(AdminTeacherManageResponseMessage message) {
    pendingService.complete(message);
  }
}