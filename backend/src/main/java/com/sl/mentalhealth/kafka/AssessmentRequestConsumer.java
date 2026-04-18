package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentRequestMessage;
import com.sl.mentalhealth.kafka.message.AssessmentResponseMessage;
import com.sl.mentalhealth.service.LocalAssessmentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AssessmentRequestConsumer {

  private final LocalAssessmentService localAssessmentService;
  private final AssessmentResponseProducer assessmentResponseProducer;

  public AssessmentRequestConsumer(LocalAssessmentService localAssessmentService,
      AssessmentResponseProducer assessmentResponseProducer) {
    this.localAssessmentService = localAssessmentService;
    this.assessmentResponseProducer = assessmentResponseProducer;
  }

  @KafkaListener(
      topics = KafkaTopics.ASSESSMENT_REQUEST,
      groupId = "mh-assessment-request-group",
      containerFactory = "assessmentRequestKafkaListenerContainerFactory"
  )
  public void onMessage(AssessmentRequestMessage message) {
    AssessmentResponseMessage response = localAssessmentService.handle(message);
    assessmentResponseProducer.send(response);
  }
}