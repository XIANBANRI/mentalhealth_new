package com.sl.mentalhealth.service;

import com.sl.mentalhealth.vo.CounselorProfileResponseVO;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class PendingCounselorProfileService {

  private final Map<String, CompletableFuture<CounselorProfileResponseVO>> futureMap = new ConcurrentHashMap<>();

  public CompletableFuture<CounselorProfileResponseVO> create(String correlationId) {
    CompletableFuture<CounselorProfileResponseVO> future = new CompletableFuture<>();
    futureMap.put(correlationId, future);
    return future;
  }

  public void complete(String correlationId, CounselorProfileResponseVO response) {
    CompletableFuture<CounselorProfileResponseVO> future = futureMap.remove(correlationId);
    if (future != null) {
      future.complete(response);
    }
  }

  public void completeExceptionally(String correlationId, Throwable throwable) {
    CompletableFuture<CounselorProfileResponseVO> future = futureMap.remove(correlationId);
    if (future != null) {
      future.completeExceptionally(throwable);
    }
  }

  public void remove(String correlationId) {
    futureMap.remove(correlationId);
  }
}