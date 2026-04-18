package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.message.AssessmentResponseMessage;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PendingAssessmentService {

  private final ConcurrentHashMap<String, CompletableFuture<AssessmentResponseMessage>> pendingMap =
      new ConcurrentHashMap<>();

  public CompletableFuture<AssessmentResponseMessage> create(String requestId) {
    CompletableFuture<AssessmentResponseMessage> future = new CompletableFuture<>();
    pendingMap.put(requestId, future);
    return future;
  }

  public void complete(String requestId, AssessmentResponseMessage response) {
    CompletableFuture<AssessmentResponseMessage> future = pendingMap.remove(requestId);
    if (future != null) {
      future.complete(response);
    }
  }

  public void remove(String requestId) {
    pendingMap.remove(requestId);
  }
}
