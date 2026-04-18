package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.message.AppointmentResponseMessage;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PendingAppointmentService {

  private final Map<String, CompletableFuture<AppointmentResponseMessage>> pendingMap = new ConcurrentHashMap<>();

  public CompletableFuture<AppointmentResponseMessage> createFuture(String requestId) {
    CompletableFuture<AppointmentResponseMessage> future = new CompletableFuture<>();
    pendingMap.put(requestId, future);
    return future;
  }

  public void complete(AppointmentResponseMessage response) {
    CompletableFuture<AppointmentResponseMessage> future = pendingMap.remove(response.getRequestId());
    if (future != null) {
      future.complete(response);
    }
  }

  public void remove(String requestId) {
    pendingMap.remove(requestId);
  }
}