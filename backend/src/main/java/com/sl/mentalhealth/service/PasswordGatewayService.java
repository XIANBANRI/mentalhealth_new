package com.sl.mentalhealth.service;

import com.sl.mentalhealth.dto.ResetPasswordRequest;
import com.sl.mentalhealth.kafka.ResetPasswordRequestProducer;
import com.sl.mentalhealth.kafka.message.ResetPasswordRequestMessage;
import com.sl.mentalhealth.kafka.message.ResetPasswordResponseMessage;
import com.sl.mentalhealth.vo.ResetPasswordResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class PasswordGatewayService {

  private static final Logger log = LoggerFactory.getLogger(PasswordGatewayService.class);

  private final ResetPasswordRequestProducer resetPasswordRequestProducer;
  private final PendingResetPasswordService pendingResetPasswordService;

  public PasswordGatewayService(ResetPasswordRequestProducer resetPasswordRequestProducer,
      PendingResetPasswordService pendingResetPasswordService) {
    this.resetPasswordRequestProducer = resetPasswordRequestProducer;
    this.pendingResetPasswordService = pendingResetPasswordService;
  }

  public ResetPasswordResponseVO resetPassword(ResetPasswordRequest request) {
    if (request.getRole() == null || request.getRole().trim().isEmpty()
        || request.getUsername() == null || request.getUsername().trim().isEmpty()
        || request.getPhone() == null || request.getPhone().trim().isEmpty()
        || request.getPassword() == null || request.getPassword().trim().isEmpty()) {
      throw new RuntimeException("请填写完整信息");
    }

    String requestId = UUID.randomUUID().toString();

    log.info("开始处理重置密码请求, requestId={}, role={}, username={}",
        requestId, request.getRole(), request.getUsername());

    CompletableFuture<ResetPasswordResponseMessage> future =
        pendingResetPasswordService.put(requestId);

    ResetPasswordRequestMessage message = new ResetPasswordRequestMessage(
        requestId,
        request.getRole(),
        request.getUsername(),
        request.getPhone(),
        request.getPassword(),
        LocalDateTime.now().toString()
    );

    resetPasswordRequestProducer.send(message);

    try {
      ResetPasswordResponseMessage response = future.get(8, TimeUnit.SECONDS);

      if (Boolean.TRUE.equals(response.getSuccess())) {
        return new ResetPasswordResponseVO(true, response.getMessage());
      }

      throw new RuntimeException(response.getMessage());
    } catch (TimeoutException e) {
      pendingResetPasswordService.remove(requestId);
      throw new RuntimeException("重置密码服务超时，请稍后重试");
    } catch (Exception e) {
      pendingResetPasswordService.remove(requestId);
      if (e.getCause() != null && e.getCause().getMessage() != null) {
        throw new RuntimeException(e.getCause().getMessage());
      }
      throw new RuntimeException(e.getMessage() == null ? "密码重置失败" : e.getMessage());
    }
  }
}