package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.LoginResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LoginResponseProducer {

  private static final Logger log = LoggerFactory.getLogger(LoginResponseProducer.class);

  private final KafkaTemplate<String, LoginResponseMessage> kafkaTemplate;

  public LoginResponseProducer(KafkaTemplate<String, LoginResponseMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(LoginResponseMessage message) {
    log.info("Producer发送登录响应, requestId={}, success={}",
        message.getRequestId(), message.getSuccess());
    kafkaTemplate.send(KafkaTopics.LOGIN_RESPONSE, message.getRequestId(), message);
  }
}