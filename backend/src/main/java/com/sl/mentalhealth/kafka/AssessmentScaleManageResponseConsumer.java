package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentScaleManageResponseMessage;
import com.sl.mentalhealth.service.PendingAssessmentScaleManageService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AssessmentScaleManageResponseConsumer {

  private final PendingAssessmentScaleManageService pendingAssessmentScaleManageService;

  public AssessmentScaleManageResponseConsumer(
      PendingAssessmentScaleManageService pendingAssessmentScaleManageService) {
    this.pendingAssessmentScaleManageService = pendingAssessmentScaleManageService;
  }

  @KafkaListener(
      topics = KafkaTopics.ASSESSMENT_SCALE_MANAGE_RESPONSE,
      containerFactory = "assessmentScaleManageResponseKafkaListenerContainerFactory"
  )
  public void consume(AssessmentScaleManageResponseMessage message) {
    pendingAssessmentScaleManageService.complete(message.getRequestId(), message);
  }
}