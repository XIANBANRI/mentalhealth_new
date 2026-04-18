package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentScaleManageResponseMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AssessmentScaleManageResponseProducer {

  private final KafkaTemplate<String, AssessmentScaleManageResponseMessage> kafkaTemplate;

  public AssessmentScaleManageResponseProducer(
      KafkaTemplate<String, AssessmentScaleManageResponseMessage> assessmentScaleManageResponseKafkaTemplate) {
    this.kafkaTemplate = assessmentScaleManageResponseKafkaTemplate;
  }

  public void send(AssessmentScaleManageResponseMessage message) {
    kafkaTemplate.send(KafkaTopics.ASSESSMENT_SCALE_MANAGE_RESPONSE, message.getRequestId(), message);
  }
}