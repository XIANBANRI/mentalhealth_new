package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.message.AdminCounselorManageResponseMessage;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class PendingAdminCounselorManageService {

  private static final long TIMEOUT_SECONDS = 10L;

  private final Map<String, CompletableFuture<AdminCounselorManageResponseMessage>> pendingMap =
      new ConcurrentHashMap<>();

  public CompletableFuture<AdminCounselorManageResponseMessage> create(String requestId) {
    CompletableFuture<AdminCounselorManageResponseMessage> future = new CompletableFuture<>();
    pendingMap.put(requestId, future);
    return future;
  }

  public void complete(AdminCounselorManageResponseMessage responseMessage) {
    if (responseMessage == null || responseMessage.getRequestId() == null) {
      return;
    }
    CompletableFuture<AdminCounselorManageResponseMessage> future =
        pendingMap.remove(responseMessage.getRequestId());
    if (future != null) {
      future.complete(responseMessage);
    }
  }

  public AdminCounselorManageResponseMessage await(
      String requestId,
      CompletableFuture<AdminCounselorManageResponseMessage> future) {
    try {
      return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    } catch (Exception e) {
      pendingMap.remove(requestId);
      throw new RuntimeException("管理员辅导员管理请求超时或失败：" + e.getMessage(), e);
    } finally {
      pendingMap.remove(requestId);
    }
  }
}