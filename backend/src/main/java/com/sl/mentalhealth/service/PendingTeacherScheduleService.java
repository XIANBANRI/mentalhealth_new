package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.message.TeacherScheduleResponseMessage;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.stereotype.Service;

@Service
public class PendingTeacherScheduleService {

  private final Map<String, CompletableFuture<TeacherScheduleResponseMessage>> pendingMap =
      new ConcurrentHashMap<>();

  public void put(String requestId, CompletableFuture<TeacherScheduleResponseMessage> future) {
    pendingMap.put(requestId, future);
  }

  public void complete(String requestId, TeacherScheduleResponseMessage response) {
    CompletableFuture<TeacherScheduleResponseMessage> future = pendingMap.remove(requestId);
    if (future != null) {
      future.complete(response);
    }
  }

  public TeacherScheduleResponseMessage waitResponse(String requestId, long timeoutSeconds) {
    CompletableFuture<TeacherScheduleResponseMessage> future = new CompletableFuture<>();
    put(requestId, future);

    try {
      return future.get(timeoutSeconds, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      pendingMap.remove(requestId);
      TeacherScheduleResponseMessage response = new TeacherScheduleResponseMessage();
      response.setRequestId(requestId);
      response.setSuccess(false);
      response.setMessage("老师工作时间处理超时");
      return response;
    } catch (Exception e) {
      pendingMap.remove(requestId);
      TeacherScheduleResponseMessage response = new TeacherScheduleResponseMessage();
      response.setRequestId(requestId);
      response.setSuccess(false);
      response.setMessage("老师工作时间处理失败");
      return response;
    }
  }
}