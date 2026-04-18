package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentScaleManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AssessmentScaleManageResponseMessage;
import com.sl.mentalhealth.service.LocalAssessmentScaleManageService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AssessmentScaleManageRequestConsumer {

  private final LocalAssessmentScaleManageService localAssessmentScaleManageService;
  private final AssessmentScaleManageResponseProducer assessmentScaleManageResponseProducer;

  public AssessmentScaleManageRequestConsumer(
      LocalAssessmentScaleManageService localAssessmentScaleManageService,
      AssessmentScaleManageResponseProducer assessmentScaleManageResponseProducer) {
    this.localAssessmentScaleManageService = localAssessmentScaleManageService;
    this.assessmentScaleManageResponseProducer = assessmentScaleManageResponseProducer;
  }

  @KafkaListener(
      topics = KafkaTopics.ASSESSMENT_SCALE_MANAGE_REQUEST,
      containerFactory = "assessmentScaleManageRequestKafkaListenerContainerFactory"
  )
  public void consume(AssessmentScaleManageRequestMessage message) {
    AssessmentScaleManageResponseMessage response;

    try {
      switch (message.getAction()) {
        case "IMPORT" -> response = AssessmentScaleManageResponseMessage.ok(
            message.getRequestId(),
            "导入成功",
            localAssessmentScaleManageService.importScale(message)
        );
        case "LIST" -> response = AssessmentScaleManageResponseMessage.ok(
            message.getRequestId(),
            "查询成功",
            localAssessmentScaleManageService.listAll()
        );
        case "DETAIL" -> response = AssessmentScaleManageResponseMessage.ok(
            message.getRequestId(),
            "查询成功",
            localAssessmentScaleManageService.getScaleDetail(message.getScaleId())
        );
        case "UPDATE" -> response = AssessmentScaleManageResponseMessage.ok(
            message.getRequestId(),
            "修改成功",
            localAssessmentScaleManageService.updateScale(message)
        );
        case "ENABLE" -> response = AssessmentScaleManageResponseMessage.ok(
            message.getRequestId(),
            "启用成功",
            localAssessmentScaleManageService.enableScale(message.getScaleId())
        );
        case "DISABLE" -> response = AssessmentScaleManageResponseMessage.ok(
            message.getRequestId(),
            "停用成功",
            localAssessmentScaleManageService.disableScale(message.getScaleId())
        );
        case "DELETE" -> response = AssessmentScaleManageResponseMessage.ok(
            message.getRequestId(),
            "删除成功",
            localAssessmentScaleManageService.deleteScale(message.getScaleId())
        );
        default -> response = AssessmentScaleManageResponseMessage.fail(message.getRequestId(), "不支持的操作类型");
      }
    } catch (Exception e) {
      response = AssessmentScaleManageResponseMessage.fail(message.getRequestId(), e.getMessage());
    }

    assessmentScaleManageResponseProducer.send(response);
  }
}