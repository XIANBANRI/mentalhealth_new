package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminCounselorManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminCounselorManageResponseMessage;
import com.sl.mentalhealth.service.LocalAdminCounselorManageService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AdminCounselorManageRequestConsumer {

  private final LocalAdminCounselorManageService localAdminCounselorManageService;
  private final AdminCounselorManageResponseProducer responseProducer;

  public AdminCounselorManageRequestConsumer(
      LocalAdminCounselorManageService localAdminCounselorManageService,
      AdminCounselorManageResponseProducer responseProducer) {
    this.localAdminCounselorManageService = localAdminCounselorManageService;
    this.responseProducer = responseProducer;
  }

  @KafkaListener(
      topics = KafkaTopics.ADMIN_COUNSELOR_MANAGE_REQUEST,
      containerFactory = "adminCounselorManageRequestKafkaListenerContainerFactory"
  )
  public void consume(AdminCounselorManageRequestMessage message) {
    AdminCounselorManageResponseMessage response =
        localAdminCounselorManageService.handle(message);
    responseProducer.send(response);
  }
}