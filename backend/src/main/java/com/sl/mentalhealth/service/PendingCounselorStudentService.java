package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.message.CounselorStudentResponseMessage;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class PendingCounselorStudentService {

  private final Map<String, CompletableFuture<CounselorStudentResponseMessage>> pendingMap =
      new ConcurrentHashMap<>();

  public CompletableFuture<CounselorStudentResponseMessage> create(String requestId) {
    CompletableFuture<CounselorStudentResponseMessage> future = new CompletableFuture<>();
    pendingMap.put(requestId, future);
    return future;
  }

  public void complete(CounselorStudentResponseMessage responseMessage) {
    if (responseMessage == null || responseMessage.getRequestId() == null) {
      return;
    }
    CompletableFuture<CounselorStudentResponseMessage> future =
        pendingMap.remove(responseMessage.getRequestId());
    if (future != null) {
      future.complete(responseMessage);
    }
  }

  public void remove(String requestId) {
    pendingMap.remove(requestId);
  }
}