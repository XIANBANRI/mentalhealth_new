package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.message.StudentProfileResponseMessage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class PendingStudentProfileService {

  private final ConcurrentHashMap<String, CompletableFuture<StudentProfileResponseMessage>> pendingMap =
      new ConcurrentHashMap<>();

  public CompletableFuture<StudentProfileResponseMessage> create(String requestId) {
    CompletableFuture<StudentProfileResponseMessage> future = new CompletableFuture<>();
    pendingMap.put(requestId, future);
    return future;
  }

  public void complete(String requestId, StudentProfileResponseMessage response) {
    CompletableFuture<StudentProfileResponseMessage> future = pendingMap.remove(requestId);
    if (future != null) {
      future.complete(response);
    }
  }

  public void remove(String requestId) {
    pendingMap.remove(requestId);
  }
}