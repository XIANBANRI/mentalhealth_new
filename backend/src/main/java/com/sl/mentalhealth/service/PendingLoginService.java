package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.message.LoginResponseMessage;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PendingLoginService {

  private final ConcurrentHashMap<String, CompletableFuture<LoginResponseMessage>> pendingMap =
      new ConcurrentHashMap<>();

  public CompletableFuture<LoginResponseMessage> put(String requestId) {
    CompletableFuture<LoginResponseMessage> future = new CompletableFuture<>();
    pendingMap.put(requestId, future);
    return future;
  }

  public void complete(String requestId, LoginResponseMessage response) {
    CompletableFuture<LoginResponseMessage> future = pendingMap.remove(requestId);
    if (future != null) {
      future.complete(response);
    }
  }

  public void remove(String requestId) {
    pendingMap.remove(requestId);
  }
}