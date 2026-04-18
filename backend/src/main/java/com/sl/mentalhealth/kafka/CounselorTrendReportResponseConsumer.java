package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorTrendReportResponseMessage;
import com.sl.mentalhealth.service.PendingCounselorTrendReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounselorTrendReportResponseConsumer {

  private final PendingCounselorTrendReportService pendingCounselorTrendReportService;

  @KafkaListener(
      topics = KafkaTopics.COUNSELOR_TREND_REPORT_RESPONSE,
      groupId = "mentalhealth-counselor-trend-response-group",
      containerFactory = "counselorTrendReportResponseKafkaListenerContainerFactory"
  )
  public void consume(CounselorTrendReportResponseMessage message) {
    if (Boolean.TRUE.equals(message.getSuccess())) {
      pendingCounselorTrendReportService.complete(message.getRequestId(), message.getData());
    } else {
      pendingCounselorTrendReportService.completeExceptionally(
          message.getRequestId(),
          message.getMessage() == null ? "趋势报告查询失败" : message.getMessage()
      );
    }
  }
}