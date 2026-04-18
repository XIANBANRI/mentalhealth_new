package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.LoginRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LoginRequestProducer {

  private static final Logger log = LoggerFactory.getLogger(LoginRequestProducer.class);

  private final KafkaTemplate<String, LoginRequestMessage> kafkaTemplate;

  public LoginRequestProducer(KafkaTemplate<String, LoginRequestMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(LoginRequestMessage message) {
    log.info("Producer发送登录消息, requestId={}", message.getRequestId());
    kafkaTemplate.send(KafkaTopics.LOGIN_REQUEST, message.getRequestId(), message);
  }
}