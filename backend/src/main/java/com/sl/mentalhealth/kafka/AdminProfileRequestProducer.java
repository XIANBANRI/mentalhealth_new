package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminProfileRequestMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminProfileRequestProducer {

  private final KafkaTemplate<String, AdminProfileRequestMessage> kafkaTemplate;

  public AdminProfileRequestProducer(
      @Qualifier("adminProfileRequestKafkaTemplate")
      KafkaTemplate<String, AdminProfileRequestMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(AdminProfileRequestMessage message) {
    if (message == null) {
      return;
    }
    kafkaTemplate.send(
        KafkaTopics.ADMIN_PROFILE_REQUEST,
        message.getRequestId(),
        message
    );
  }
}