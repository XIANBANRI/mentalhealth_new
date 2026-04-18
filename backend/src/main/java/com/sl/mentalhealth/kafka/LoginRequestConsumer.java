package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.LoginRequestMessage;
import com.sl.mentalhealth.kafka.message.LoginResponseMessage;
import com.sl.mentalhealth.service.LocalAuthService;
import com.sl.mentalhealth.vo.LoginResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LoginRequestConsumer {

  private static final Logger log = LoggerFactory.getLogger(LoginRequestConsumer.class);

  private final LocalAuthService localAuthService;
  private final LoginResponseProducer loginResponseProducer;

  public LoginRequestConsumer(LocalAuthService localAuthService,
      LoginResponseProducer loginResponseProducer) {
    this.localAuthService = localAuthService;
    this.loginResponseProducer = loginResponseProducer;
  }

  @KafkaListener(
      topics = KafkaTopics.LOGIN_REQUEST,
      groupId = "mh-login-request-group",
      containerFactory = "loginRequestKafkaListenerContainerFactory"
  )
  public void onMessage(LoginRequestMessage message) {
    log.info("Consumer收到登录请求, requestId={}, role={}, username={}",
        message.getRequestId(), message.getRole(), message.getUsername());

    LoginResponseMessage response;

    try {
      LoginResponseVO vo = localAuthService.login(
          message.getRole(),
          message.getUsername(),
          message.getPassword()
      );

      response = new LoginResponseMessage(
          message.getRequestId(),
          true,
          "登录成功",
          vo.getRole(),
          vo.getUsername(),
          vo.getRedirectPath()
      );
    } catch (Exception e) {
      response = new LoginResponseMessage(
          message.getRequestId(),
          false,
          e.getMessage() == null ? "登录失败" : e.getMessage(),
          message.getRole(),
          message.getUsername(),
          null
      );
    }

    loginResponseProducer.send(response);

    log.info("Consumer发送登录响应, requestId={}, success={}",
        response.getRequestId(), response.getSuccess());
  }
}