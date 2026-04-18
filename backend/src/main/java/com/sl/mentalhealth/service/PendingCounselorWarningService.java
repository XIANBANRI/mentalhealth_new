package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.message.CounselorWarningResponseMessage;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class PendingCounselorWarningService {

  private final Map<String, CompletableFuture<CounselorWarningResponseMessage>> pendingMap =
      new ConcurrentHashMap<>();

  public CompletableFuture<CounselorWarningResponseMessage> create(String requestId) {
    CompletableFuture<CounselorWarningResponseMessage> future = new CompletableFuture<>();
    pendingMap.put(requestId, future);
    return future;
  }

  public void complete(CounselorWarningResponseMessage responseMessage) {
    if (responseMessage == null || responseMessage.getRequestId() == null) {
      return;
    }
    CompletableFuture<CounselorWarningResponseMessage> future =
        pendingMap.remove(responseMessage.getRequestId());
    if (future != null) {
      future.complete(responseMessage);
    }
  }

  public void remove(String requestId) {
    pendingMap.remove(requestId);
  }
}