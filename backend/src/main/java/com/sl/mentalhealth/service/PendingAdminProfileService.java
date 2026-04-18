package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.message.AdminProfileResponseMessage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Service;

@Service
public class PendingAdminProfileService {

  private final ConcurrentMap<String, CompletableFuture<AdminProfileResponseMessage>> pendingMap =
      new ConcurrentHashMap<>();

  public CompletableFuture<AdminProfileResponseMessage> create(String requestId) {
    CompletableFuture<AdminProfileResponseMessage> future = new CompletableFuture<>();
    pendingMap.put(requestId, future);
    return future;
  }

  public void complete(AdminProfileResponseMessage message) {
    if (message == null || message.getRequestId() == null) {
      return;
    }

    CompletableFuture<AdminProfileResponseMessage> future = pendingMap.remove(message.getRequestId());
    if (future != null) {
      future.complete(message);
    }
  }

  public void remove(String requestId) {
    if (requestId != null) {
      pendingMap.remove(requestId);
    }
  }
}