package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.message.ResetPasswordResponseMessage;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PendingResetPasswordService {

  private final ConcurrentHashMap<String, CompletableFuture<ResetPasswordResponseMessage>> pendingMap =
      new ConcurrentHashMap<>();

  public CompletableFuture<ResetPasswordResponseMessage> put(String requestId) {
    CompletableFuture<ResetPasswordResponseMessage> future = new CompletableFuture<>();
    pendingMap.put(requestId, future);
    return future;
  }

  public void complete(String requestId, ResetPasswordResponseMessage response) {
    CompletableFuture<ResetPasswordResponseMessage> future = pendingMap.remove(requestId);
    if (future != null) {
      future.complete(response);
    }
  }

  public void remove(String requestId) {
    pendingMap.remove(requestId);
  }
}