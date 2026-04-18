package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.message.AdminTeacherManageResponseMessage;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class PendingAdminTeacherManageService {

  private static final long TIMEOUT_SECONDS = 10L;

  private final Map<String, CompletableFuture<AdminTeacherManageResponseMessage>> pendingMap =
      new ConcurrentHashMap<>();

  public CompletableFuture<AdminTeacherManageResponseMessage> create(String requestId) {
    CompletableFuture<AdminTeacherManageResponseMessage> future = new CompletableFuture<>();
    pendingMap.put(requestId, future);
    return future;
  }

  public void complete(AdminTeacherManageResponseMessage responseMessage) {
    if (responseMessage == null || responseMessage.getRequestId() == null) {
      return;
    }
    CompletableFuture<AdminTeacherManageResponseMessage> future =
        pendingMap.remove(responseMessage.getRequestId());
    if (future != null) {
      future.complete(responseMessage);
    }
  }

  public AdminTeacherManageResponseMessage await(
      String requestId, CompletableFuture<AdminTeacherManageResponseMessage> future) {
    try {
      return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    } catch (Exception e) {
      pendingMap.remove(requestId);
      throw new RuntimeException("管理员教师管理请求超时或失败：" + e.getMessage(), e);
    } finally {
      pendingMap.remove(requestId);
    }
  }
}