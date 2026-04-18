package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminTeacherManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminTeacherManageResponseMessage;
import com.sl.mentalhealth.service.LocalAdminTeacherManageService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AdminTeacherManageRequestConsumer {

  private final LocalAdminTeacherManageService localAdminTeacherManageService;
  private final AdminTeacherManageResponseProducer responseProducer;

  public AdminTeacherManageRequestConsumer(
      LocalAdminTeacherManageService localAdminTeacherManageService,
      AdminTeacherManageResponseProducer responseProducer) {
    this.localAdminTeacherManageService = localAdminTeacherManageService;
    this.responseProducer = responseProducer;
  }

  @KafkaListener(
      topics = KafkaTopics.ADMIN_TEACHER_MANAGE_REQUEST,
      containerFactory = "adminTeacherManageRequestKafkaListenerContainerFactory"
  )
  public void consume(AdminTeacherManageRequestMessage message) {
    AdminTeacherManageResponseMessage response = localAdminTeacherManageService.handle(message);
    responseProducer.send(response);
  }
}