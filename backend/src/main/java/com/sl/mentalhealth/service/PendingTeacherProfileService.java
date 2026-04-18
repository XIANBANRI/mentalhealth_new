package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.message.TeacherProfileResponseMessage;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.stereotype.Service;

@Service
public class PendingTeacherProfileService {

  private final Map<String, CompletableFuture<TeacherProfileResponseMessage>> pendingMap =
      new ConcurrentHashMap<>();

  public CompletableFuture<TeacherProfileResponseMessage> create(String requestId) {
    CompletableFuture<TeacherProfileResponseMessage> future = new CompletableFuture<>();
    pendingMap.put(requestId, future);
    return future;
  }

  public void complete(String requestId, TeacherProfileResponseMessage response) {
    CompletableFuture<TeacherProfileResponseMessage> future = pendingMap.remove(requestId);
    if (future != null) {
      future.complete(response);
    }
  }

  public void remove(String requestId) {
    pendingMap.remove(requestId);
  }

  public TeacherProfileResponseMessage buildTimeoutResponse(String requestId) {
    TeacherProfileResponseMessage response = new TeacherProfileResponseMessage();
    response.setRequestId(requestId);
    response.setSuccess(false);
    response.setMessage("老师信息处理超时");
    response.setData(null);
    return response;
  }

  public TeacherProfileResponseMessage buildErrorResponse(String requestId, String message) {
    TeacherProfileResponseMessage response = new TeacherProfileResponseMessage();
    response.setRequestId(requestId);
    response.setSuccess(false);
    response.setMessage(message);
    response.setData(null);
    return response;
  }
}