package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorTrendReportResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounselorTrendReportResponseProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void send(CounselorTrendReportResponseMessage message) {
    kafkaTemplate.send(KafkaTopics.COUNSELOR_TREND_REPORT_RESPONSE, message.getRequestId(), message);
  }
}