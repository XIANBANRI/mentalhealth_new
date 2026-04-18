package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.LoginResponseMessage;
import com.sl.mentalhealth.service.PendingLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LoginResponseConsumer {

  private static final Logger log = LoggerFactory.getLogger(LoginResponseConsumer.class);

  private final PendingLoginService pendingLoginService;

  public LoginResponseConsumer(PendingLoginService pendingLoginService) {
    this.pendingLoginService = pendingLoginService;
  }

  @KafkaListener(
      topics = KafkaTopics.LOGIN_RESPONSE,
      groupId = "mh-login-response-group",
      containerFactory = "loginResponseKafkaListenerContainerFactory"
  )
  public void onMessage(LoginResponseMessage message) {
    log.info("收到登录响应消息, requestId={}, success={}",
        message.getRequestId(), message.getSuccess());

    pendingLoginService.complete(message.getRequestId(), message);
  }
}