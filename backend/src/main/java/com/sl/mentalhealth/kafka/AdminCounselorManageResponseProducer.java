package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminCounselorManageResponseMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminCounselorManageResponseProducer {

  private final KafkaTemplate<String, AdminCounselorManageResponseMessage> kafkaTemplate;

  public AdminCounselorManageResponseProducer(
      @Qualifier("adminCounselorManageResponseKafkaTemplate")
      KafkaTemplate<String, AdminCounselorManageResponseMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(AdminCounselorManageResponseMessage message) {
    kafkaTemplate.send(KafkaTopics.ADMIN_COUNSELOR_MANAGE_RESPONSE,
        message.getRequestId(), message);
  }
}