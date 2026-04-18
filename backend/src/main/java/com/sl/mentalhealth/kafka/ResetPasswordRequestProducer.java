package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.ResetPasswordRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordRequestProducer {

  private static final Logger log = LoggerFactory.getLogger(ResetPasswordRequestProducer.class);

  private final KafkaTemplate<String, ResetPasswordRequestMessage> kafkaTemplate;

  public ResetPasswordRequestProducer(
      KafkaTemplate<String, ResetPasswordRequestMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(ResetPasswordRequestMessage message) {
    log.info("Producer发送重置密码请求, requestId={}", message.getRequestId());
    kafkaTemplate.send(KafkaTopics.RESET_PASSWORD_REQUEST, message.getRequestId(), message);
  }
}