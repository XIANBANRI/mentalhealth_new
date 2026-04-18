package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorProfileRequestMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CounselorProfileRequestProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public CounselorProfileRequestProducer(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(CounselorProfileRequestMessage message) {
    kafkaTemplate.send(KafkaTopics.COUNSELOR_PROFILE_REQUEST, message.getCorrelationId(), message);
  }
}