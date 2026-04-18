package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.message.TeacherAppointmentResponseMessage;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.stereotype.Service;

@Service
public class PendingTeacherAppointmentService {

  private final Map<String, CompletableFuture<TeacherAppointmentResponseMessage>> pendingMap =
      new ConcurrentHashMap<>();

  public void put(String requestId, CompletableFuture<TeacherAppointmentResponseMessage> future) {
    pendingMap.put(requestId, future);
  }

  public void complete(String requestId, TeacherAppointmentResponseMessage response) {
    CompletableFuture<TeacherAppointmentResponseMessage> future = pendingMap.remove(requestId);
    if (future != null) {
      future.complete(response);
    }
  }

  public TeacherAppointmentResponseMessage waitResponse(String requestId, long timeoutSeconds) {
    CompletableFuture<TeacherAppointmentResponseMessage> future = new CompletableFuture<>();
    put(requestId, future);

    try {
      return future.get(timeoutSeconds, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      pendingMap.remove(requestId);
      TeacherAppointmentResponseMessage response = new TeacherAppointmentResponseMessage();
      response.setRequestId(requestId);
      response.setSuccess(false);
      response.setMessage("老师预约管理处理超时");
      return response;
    } catch (Exception e) {
      pendingMap.remove(requestId);
      TeacherAppointmentResponseMessage response = new TeacherAppointmentResponseMessage();
      response.setRequestId(requestId);
      response.setSuccess(false);
      response.setMessage("老师预约管理处理失败");
      return response;
    }
  }
}