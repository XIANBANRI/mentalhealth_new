package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentScaleManageRequestMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AssessmentScaleManageRequestProducer {

  private final KafkaTemplate<String, AssessmentScaleManageRequestMessage> kafkaTemplate;

  public AssessmentScaleManageRequestProducer(
      KafkaTemplate<String, AssessmentScaleManageRequestMessage> assessmentScaleManageRequestKafkaTemplate) {
    this.kafkaTemplate = assessmentScaleManageRequestKafkaTemplate;
  }

  public void send(AssessmentScaleManageRequestMessage message) {
    kafkaTemplate.send(KafkaTopics.ASSESSMENT_SCALE_MANAGE_REQUEST, message.getRequestId(), message);
  }
}