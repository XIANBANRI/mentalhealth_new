package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.ResetPasswordResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordResponseProducer {

  private static final Logger log = LoggerFactory.getLogger(ResetPasswordResponseProducer.class);

  private final KafkaTemplate<String, ResetPasswordResponseMessage> kafkaTemplate;

  public ResetPasswordResponseProducer(
      KafkaTemplate<String, ResetPasswordResponseMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(ResetPasswordResponseMessage message) {
    log.info("Producer发送重置密码响应, requestId={}, success={}",
        message.getRequestId(), message.getSuccess());
    kafkaTemplate.send(KafkaTopics.RESET_PASSWORD_RESPONSE, message.getRequestId(), message);
  }
}