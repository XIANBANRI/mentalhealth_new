package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentResponseMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AssessmentResponseProducer {

  private final KafkaTemplate<String, AssessmentResponseMessage> kafkaTemplate;

  public AssessmentResponseProducer(KafkaTemplate<String, AssessmentResponseMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(AssessmentResponseMessage message) {
    kafkaTemplate.send(KafkaTopics.ASSESSMENT_RESPONSE, message.getRequestId(), message);
  }
}