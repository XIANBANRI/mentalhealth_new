package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminCounselorManageRequestMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminCounselorManageRequestProducer {

  private final KafkaTemplate<String, AdminCounselorManageRequestMessage> kafkaTemplate;

  public AdminCounselorManageRequestProducer(
      @Qualifier("adminCounselorManageRequestKafkaTemplate")
      KafkaTemplate<String, AdminCounselorManageRequestMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(AdminCounselorManageRequestMessage message) {
    kafkaTemplate.send(KafkaTopics.ADMIN_COUNSELOR_MANAGE_REQUEST,
        message.getRequestId(), message);
  }
}