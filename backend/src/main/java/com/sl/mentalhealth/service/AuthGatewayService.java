package com.sl.mentalhealth.service;

import com.sl.mentalhealth.common.JwtUtil;
import com.sl.mentalhealth.dto.LoginRequest;
import com.sl.mentalhealth.kafka.LoginRequestProducer;
import com.sl.mentalhealth.kafka.message.LoginRequestMessage;
import com.sl.mentalhealth.kafka.message.LoginResponseMessage;
import com.sl.mentalhealth.vo.LoginResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class AuthGatewayService {

  private static final Logger log = LoggerFactory.getLogger(AuthGatewayService.class);

  private final LoginRequestProducer loginRequestProducer;
  private final PendingLoginService pendingLoginService;

  public AuthGatewayService(LoginRequestProducer loginRequestProducer,
      PendingLoginService pendingLoginService) {
    this.loginRequestProducer = loginRequestProducer;
    this.pendingLoginService = pendingLoginService;
  }

  public LoginResponseVO login(LoginRequest request) {
    if (request == null
        || request.getRole() == null || request.getRole().trim().isEmpty()
        || request.getUsername() == null || request.getUsername().trim().isEmpty()
        || request.getPassword() == null || request.getPassword().trim().isEmpty()) {
      throw new IllegalArgumentException("请填写完整信息");
    }

    String requestId = UUID.randomUUID().toString();
    log.info("开始处理登录请求, requestId={}, role={}, username={}",
        requestId, request.getRole(), request.getUsername());

    CompletableFuture<LoginResponseMessage> future = pendingLoginService.put(requestId);

    LoginRequestMessage message = new LoginRequestMessage(
        requestId,
        request.getRole(),
        request.getUsername(),
        request.getPassword(),
        LocalDateTime.now().toString()
    );

    try {
      log.info("发送登录请求到Kafka, requestId={}", requestId);
      loginRequestProducer.send(message);

      LoginResponseMessage response = future.get(15, TimeUnit.SECONDS);

      log.info("收到Kafka登录响应, requestId={}, success={}, message={}",
          requestId, response.getSuccess(), response.getMessage());

      if (Boolean.TRUE.equals(response.getSuccess())) {
        String token = JwtUtil.generateToken(response.getUsername(), response.getRole());

        return new LoginResponseVO(
            response.getRole(),
            response.getUsername(),
            response.getRedirectPath(),
            token
        );
      }

      throw new RuntimeException(response.getMessage());
    } catch (TimeoutException e) {
      log.error("登录等待Kafka响应超时, requestId={}", requestId, e);
      pendingLoginService.remove(requestId);
      throw new RuntimeException("登录服务超时，请检查 Kafka 是否正常启动");
    } catch (IllegalArgumentException e) {
      log.error("登录参数异常, requestId={}, message={}", requestId, e.getMessage(), e);
      pendingLoginService.remove(requestId);
      throw e;
    } catch (Exception e) {
      log.error("登录处理异常, requestId={}, message={}", requestId, e.getMessage(), e);
      pendingLoginService.remove(requestId);

      if (e.getCause() != null && e.getCause().getMessage() != null) {
        throw new RuntimeException(e.getCause().getMessage());
      }
      throw new RuntimeException(e.getMessage() == null ? "登录失败" : e.getMessage());
    }
  }
}