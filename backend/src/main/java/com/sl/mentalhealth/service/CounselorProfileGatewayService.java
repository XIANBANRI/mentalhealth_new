package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.CounselorProfileRequestProducer;
import com.sl.mentalhealth.kafka.message.CounselorProfileRequestMessage;
import com.sl.mentalhealth.vo.CounselorProfileResponseVO;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CounselorProfileGatewayService {

  private final CounselorProfileRequestProducer counselorProfileRequestProducer;
  private final PendingCounselorProfileService pendingCounselorProfileService;

  @Value("${app.kafka.counselor-profile-timeout-seconds:10}")
  private long timeoutSeconds;

  public CounselorProfileGatewayService(
      CounselorProfileRequestProducer counselorProfileRequestProducer,
      PendingCounselorProfileService pendingCounselorProfileService) {
    this.counselorProfileRequestProducer = counselorProfileRequestProducer;
    this.pendingCounselorProfileService = pendingCounselorProfileService;
  }

  public CounselorProfileResponseVO getProfile(String account) {
    String correlationId = UUID.randomUUID().toString();

    CompletableFuture<CounselorProfileResponseVO> future =
        pendingCounselorProfileService.create(correlationId);

    CounselorProfileRequestMessage message = new CounselorProfileRequestMessage();
    message.setCorrelationId(correlationId);
    message.setAction(CounselorProfileRequestMessage.ACTION_QUERY_PROFILE);
    message.setAccount(account);

    counselorProfileRequestProducer.send(message);

    try {
      return future.get(timeoutSeconds, TimeUnit.SECONDS);
    } catch (Exception e) {
      pendingCounselorProfileService.remove(correlationId);
      throw new RuntimeException("获取辅导员个人信息超时或失败：" + e.getMessage(), e);
    }
  }

  public CounselorProfileResponseVO updateAvatar(String account, String avatarUrl) {
    String correlationId = UUID.randomUUID().toString();

    CompletableFuture<CounselorProfileResponseVO> future =
        pendingCounselorProfileService.create(correlationId);

    CounselorProfileRequestMessage message = new CounselorProfileRequestMessage();
    message.setCorrelationId(correlationId);
    message.setAction(CounselorProfileRequestMessage.ACTION_UPDATE_AVATAR);
    message.setAccount(account);
    message.setAvatarUrl(avatarUrl);

    counselorProfileRequestProducer.send(message);

    try {
      return future.get(timeoutSeconds, TimeUnit.SECONDS);
    } catch (Exception e) {
      pendingCounselorProfileService.remove(correlationId);
      throw new RuntimeException("更新辅导员头像超时或失败：" + e.getMessage(), e);
    }
  }
}