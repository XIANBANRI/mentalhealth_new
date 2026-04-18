package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentRequestMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AssessmentRequestProducer {

  private final KafkaTemplate<String, AssessmentRequestMessage> kafkaTemplate;

  public AssessmentRequestProducer(KafkaTemplate<String, AssessmentRequestMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(AssessmentRequestMessage message) {
    kafkaTemplate.send(KafkaTopics.ASSESSMENT_REQUEST, message.getRequestId(), message);
  }
}