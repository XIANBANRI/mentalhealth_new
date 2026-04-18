package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.AdminProfileRequestProducer;
import com.sl.mentalhealth.kafka.message.AdminProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminProfileResponseMessage;
import com.sl.mentalhealth.vo.AdminProfileResponseVO;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminProfileGatewayService {

  private final AdminProfileRequestProducer adminProfileRequestProducer;
  private final PendingAdminProfileService pendingAdminProfileService;

  @Value("${app.kafka.admin-profile-timeout-seconds:10}")
  private long timeoutSeconds;

  public AdminProfileGatewayService(
      AdminProfileRequestProducer adminProfileRequestProducer,
      PendingAdminProfileService pendingAdminProfileService) {
    this.adminProfileRequestProducer = adminProfileRequestProducer;
    this.pendingAdminProfileService = pendingAdminProfileService;
  }

  public AdminProfileResponseVO getAdminProfile(String account) {
    if (account == null || account.trim().isEmpty()) {
      throw new IllegalArgumentException("管理员账号不能为空");
    }

    String requestId = UUID.randomUUID().toString();
    CompletableFuture<AdminProfileResponseMessage> future =
        pendingAdminProfileService.create(requestId);

    try {
      AdminProfileRequestMessage requestMessage =
          new AdminProfileRequestMessage(
              requestId,
              AdminProfileRequestMessage.ACTION_QUERY_PROFILE,
              account.trim(),
              null
          );
      adminProfileRequestProducer.send(requestMessage);

      AdminProfileResponseMessage response = future.get(timeoutSeconds, TimeUnit.SECONDS);

      if (response == null) {
        throw new RuntimeException("查询管理员信息失败");
      }

      if (!response.isSuccess()) {
        throw new RuntimeException(response.getMessage());
      }

      return response.getData();
    } catch (Exception e) {
      throw new RuntimeException("查询管理员信息失败：" + e.getMessage(), e);
    } finally {
      pendingAdminProfileService.remove(requestId);
    }
  }

  public AdminProfileResponseVO updateAvatar(String account, String avatarUrl) {
    if (account == null || account.trim().isEmpty()) {
      throw new IllegalArgumentException("管理员账号不能为空");
    }
    if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
      throw new IllegalArgumentException("头像地址不能为空");
    }

    String requestId = UUID.randomUUID().toString();
    CompletableFuture<AdminProfileResponseMessage> future =
        pendingAdminProfileService.create(requestId);

    try {
      AdminProfileRequestMessage requestMessage =
          new AdminProfileRequestMessage(
              requestId,
              AdminProfileRequestMessage.ACTION_UPDATE_AVATAR,
              account.trim(),
              avatarUrl.trim()
          );
      adminProfileRequestProducer.send(requestMessage);

      AdminProfileResponseMessage response = future.get(timeoutSeconds, TimeUnit.SECONDS);

      if (response == null) {
        throw new RuntimeException("更新管理员头像失败");
      }

      if (!response.isSuccess()) {
        throw new RuntimeException(response.getMessage());
      }

      return response.getData();
    } catch (Exception e) {
      throw new RuntimeException("更新管理员头像失败：" + e.getMessage(), e);
    } finally {
      pendingAdminProfileService.remove(requestId);
    }
  }
}