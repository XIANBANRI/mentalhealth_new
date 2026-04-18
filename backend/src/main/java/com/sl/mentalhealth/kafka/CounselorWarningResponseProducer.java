package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorWarningResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounselorWarningResponseProducer {

  @Qualifier("counselorWarningResponseKafkaTemplate")
  private final KafkaTemplate<String, CounselorWarningResponseMessage> kafkaTemplate;

  public void send(CounselorWarningResponseMessage message) {
    kafkaTemplate.send(KafkaTopics.COUNSELOR_WARNING_RESPONSE, message.getRequestId(), message);
  }
}