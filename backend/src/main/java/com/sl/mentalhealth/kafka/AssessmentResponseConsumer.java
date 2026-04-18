package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentResponseMessage;
import com.sl.mentalhealth.service.PendingAssessmentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AssessmentResponseConsumer {

  private final PendingAssessmentService pendingAssessmentService;

  public AssessmentResponseConsumer(PendingAssessmentService pendingAssessmentService) {
    this.pendingAssessmentService = pendingAssessmentService;
  }

  @KafkaListener(
      topics = KafkaTopics.ASSESSMENT_RESPONSE,
      groupId = "mh-assessment-response-group",
      containerFactory = "assessmentResponseKafkaListenerContainerFactory"
  )
  public void onMessage(AssessmentResponseMessage message) {
    pendingAssessmentService.complete(message.getRequestId(), message);
  }
}