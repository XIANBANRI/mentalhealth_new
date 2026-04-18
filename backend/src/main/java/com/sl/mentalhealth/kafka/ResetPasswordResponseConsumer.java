package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.ResetPasswordResponseMessage;
import com.sl.mentalhealth.service.PendingResetPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordResponseConsumer {

  private static final Logger log = LoggerFactory.getLogger(ResetPasswordResponseConsumer.class);

  private final PendingResetPasswordService pendingResetPasswordService;

  public ResetPasswordResponseConsumer(PendingResetPasswordService pendingResetPasswordService) {
    this.pendingResetPasswordService = pendingResetPasswordService;
  }

  @KafkaListener(
      topics = KafkaTopics.RESET_PASSWORD_RESPONSE,
      groupId = "mh-reset-password-response-group",
      containerFactory = "resetPasswordResponseKafkaListenerContainerFactory"
  )
  public void onMessage(ResetPasswordResponseMessage message) {
    log.info("收到重置密码响应消息, requestId={}, success={}",
        message.getRequestId(), message.getSuccess());

    pendingResetPasswordService.complete(message.getRequestId(), message);
  }
}