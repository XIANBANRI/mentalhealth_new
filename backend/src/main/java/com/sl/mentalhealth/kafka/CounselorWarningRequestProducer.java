package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorWarningRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounselorWarningRequestProducer {

  @Qualifier("counselorWarningRequestKafkaTemplate")
  private final KafkaTemplate<String, CounselorWarningRequestMessage> kafkaTemplate;

  public void send(CounselorWarningRequestMessage message) {
    kafkaTemplate.send(KafkaTopics.COUNSELOR_WARNING_REQUEST, message.getRequestId(), message);
  }
}