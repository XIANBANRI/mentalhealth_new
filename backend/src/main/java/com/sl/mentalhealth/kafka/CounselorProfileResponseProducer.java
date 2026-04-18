package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorProfileResponseMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CounselorProfileResponseProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public CounselorProfileResponseProducer(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(CounselorProfileResponseMessage message) {
    kafkaTemplate.send(KafkaTopics.COUNSELOR_PROFILE_RESPONSE, message.getCorrelationId(), message);
  }
}