package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.message.AssessmentScaleManageResponseMessage;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class PendingAssessmentScaleManageService {

  private final Map<String, CompletableFuture<AssessmentScaleManageResponseMessage>> pendingMap =
      new ConcurrentHashMap<>();

  public CompletableFuture<AssessmentScaleManageResponseMessage> create(String requestId) {
    CompletableFuture<AssessmentScaleManageResponseMessage> future = new CompletableFuture<>();
    pendingMap.put(requestId, future);
    return future;
  }

  public void complete(String requestId, AssessmentScaleManageResponseMessage response) {
    CompletableFuture<AssessmentScaleManageResponseMessage> future = pendingMap.remove(requestId);
    if (future != null) {
      future.complete(response);
    }
  }

  public void remove(String requestId) {
    pendingMap.remove(requestId);
  }
}