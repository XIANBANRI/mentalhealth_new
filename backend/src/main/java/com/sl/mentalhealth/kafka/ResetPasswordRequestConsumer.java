package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.ResetPasswordRequestMessage;
import com.sl.mentalhealth.kafka.message.ResetPasswordResponseMessage;
import com.sl.mentalhealth.service.LocalPasswordService;
import com.sl.mentalhealth.vo.ResetPasswordResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordRequestConsumer {

  private static final Logger log = LoggerFactory.getLogger(ResetPasswordRequestConsumer.class);

  private final LocalPasswordService localPasswordService;
  private final ResetPasswordResponseProducer resetPasswordResponseProducer;

  public ResetPasswordRequestConsumer(LocalPasswordService localPasswordService,
      ResetPasswordResponseProducer resetPasswordResponseProducer) {
    this.localPasswordService = localPasswordService;
    this.resetPasswordResponseProducer = resetPasswordResponseProducer;
  }

  @KafkaListener(
      topics = KafkaTopics.RESET_PASSWORD_REQUEST,
      groupId = "mh-reset-password-request-group",
      containerFactory = "resetPasswordRequestKafkaListenerContainerFactory"
  )
  public void onMessage(ResetPasswordRequestMessage message) {
    log.info("Consumer收到重置密码请求, requestId={}, role={}, username={}",
        message.getRequestId(), message.getRole(), message.getUsername());

    ResetPasswordResponseMessage response;

    try {
      ResetPasswordResponseVO vo = localPasswordService.resetPassword(
          message.getRole(),
          message.getUsername(),
          message.getPhone(),
          message.getNewPassword()
      );

      response = new ResetPasswordResponseMessage(
          message.getRequestId(),
          true,
          vo.getMessage()
      );
    } catch (Exception e) {
      response = new ResetPasswordResponseMessage(
          message.getRequestId(),
          false,
          e.getMessage() == null ? "密码重置失败" : e.getMessage()
      );
    }

    resetPasswordResponseProducer.send(response);

    log.info("Consumer发送重置密码响应, requestId={}, success={}",
        response.getRequestId(), response.getSuccess());
  }
}