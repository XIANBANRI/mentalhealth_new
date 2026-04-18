package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorTrendReportRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounselorTrendReportRequestProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void send(CounselorTrendReportRequestMessage message) {
    kafkaTemplate.send(KafkaTopics.COUNSELOR_TREND_REPORT_REQUEST, message.getRequestId(), message);
  }
}