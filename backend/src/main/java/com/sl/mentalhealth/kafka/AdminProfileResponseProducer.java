package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminProfileResponseMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminProfileResponseProducer {

  private final KafkaTemplate<String, AdminProfileResponseMessage> kafkaTemplate;

  public AdminProfileResponseProducer(
      @Qualifier("adminProfileResponseKafkaTemplate")
      KafkaTemplate<String, AdminProfileResponseMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(AdminProfileResponseMessage message) {
    if (message == null) {
      return;
    }
    kafkaTemplate.send(
        KafkaTopics.ADMIN_PROFILE_RESPONSE,
        message.getRequestId(),
        message
    );
  }
}