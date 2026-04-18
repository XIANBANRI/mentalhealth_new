package com.sl.mentalhealth.service;

import com.sl.mentalhealth.dto.AssessmentScaleUpdateRequest;
import com.sl.mentalhealth.kafka.AssessmentScaleManageRequestProducer;
import com.sl.mentalhealth.kafka.message.AssessmentScaleManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AssessmentScaleManageResponseMessage;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class AssessmentScaleManageGatewayService {

  private final AssessmentScaleManageRequestProducer assessmentScaleManageRequestProducer;
  private final PendingAssessmentScaleManageService pendingAssessmentScaleManageService;

  public AssessmentScaleManageGatewayService(
      AssessmentScaleManageRequestProducer assessmentScaleManageRequestProducer,
      PendingAssessmentScaleManageService pendingAssessmentScaleManageService) {
    this.assessmentScaleManageRequestProducer = assessmentScaleManageRequestProducer;
    this.pendingAssessmentScaleManageService = pendingAssessmentScaleManageService;
  }

  public AssessmentScaleManageResponseMessage importScale(AssessmentScaleManageRequestMessage message) {
    return sendAndAwait(message);
  }

  public AssessmentScaleManageResponseMessage listAll() {
    AssessmentScaleManageRequestMessage message = new AssessmentScaleManageRequestMessage();
    message.setRequestId(UUID.randomUUID().toString());
    message.setAction("LIST");
    return sendAndAwait(message);
  }

  public AssessmentScaleManageResponseMessage detail(Long scaleId) {
    AssessmentScaleManageRequestMessage message = new AssessmentScaleManageRequestMessage();
    message.setRequestId(UUID.randomUUID().toString());
    message.setAction("DETAIL");
    message.setScaleId(scaleId);
    return sendAndAwait(message);
  }

  public AssessmentScaleManageResponseMessage update(AssessmentScaleUpdateRequest request) {
    AssessmentScaleManageRequestMessage message = new AssessmentScaleManageRequestMessage();
    message.setRequestId(UUID.randomUUID().toString());
    message.setAction("UPDATE");
    message.setScaleId(request.getScaleId());
    message.setScaleName(request.getScaleName());
    message.setScaleType(request.getScaleType());
    message.setDescription(request.getDescription());
    message.setOperator(request.getOperator());
    message.setVersionRemark(request.getVersionRemark());
    message.setQuestions(request.getQuestions());
    message.setRules(request.getRules());
    return sendAndAwait(message);
  }

  public AssessmentScaleManageResponseMessage enable(Long scaleId) {
    AssessmentScaleManageRequestMessage message = new AssessmentScaleManageRequestMessage();
    message.setRequestId(UUID.randomUUID().toString());
    message.setAction("ENABLE");
    message.setScaleId(scaleId);
    return sendAndAwait(message);
  }

  public AssessmentScaleManageResponseMessage disable(Long scaleId) {
    AssessmentScaleManageRequestMessage message = new AssessmentScaleManageRequestMessage();
    message.setRequestId(UUID.randomUUID().toString());
    message.setAction("DISABLE");
    message.setScaleId(scaleId);
    return sendAndAwait(message);
  }

  public AssessmentScaleManageResponseMessage delete(Long scaleId) {
    AssessmentScaleManageRequestMessage message = new AssessmentScaleManageRequestMessage();
    message.setRequestId(UUID.randomUUID().toString());
    message.setAction("DELETE");
    message.setScaleId(scaleId);
    return sendAndAwait(message);
  }

  private AssessmentScaleManageResponseMessage sendAndAwait(AssessmentScaleManageRequestMessage message) {
    CompletableFuture<AssessmentScaleManageResponseMessage> future =
        pendingAssessmentScaleManageService.create(message.getRequestId());

    assessmentScaleManageRequestProducer.send(message);

    try {
      return future.get(15, TimeUnit.SECONDS);
    } catch (Exception e) {
      pendingAssessmentScaleManageService.remove(message.getRequestId());
      return AssessmentScaleManageResponseMessage.fail(message.getRequestId(), "Kafka请求超时：" + e.getMessage());
    }
  }
}