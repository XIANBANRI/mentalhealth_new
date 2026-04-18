package com.sl.mentalhealth.service;

import com.sl.mentalhealth.vo.CounselorTrendReportVO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class PendingCounselorTrendReportService {

  private final Map<String, CompletableFuture<CounselorTrendReportVO>> pendingMap = new ConcurrentHashMap<>();

  public void create(String requestId) {
    pendingMap.put(requestId, new CompletableFuture<>());
  }

  public CounselorTrendReportVO await(String requestId, long timeout, TimeUnit timeUnit) {
    CompletableFuture<CounselorTrendReportVO> future = pendingMap.get(requestId);
    if (future == null) {
      throw new RuntimeException("趋势报告请求不存在或已过期");
    }
    try {
      return future.get(timeout, timeUnit);
    } catch (TimeoutException e) {
      throw new RuntimeException("趋势报告查询超时");
    } catch (Exception e) {
      throw new RuntimeException("趋势报告查询失败：" + e.getMessage(), e);
    } finally {
      pendingMap.remove(requestId);
    }
  }

  public void complete(String requestId, CounselorTrendReportVO data) {
    CompletableFuture<CounselorTrendReportVO> future = pendingMap.get(requestId);
    if (future != null) {
      future.complete(data);
    }
  }

  public void completeExceptionally(String requestId, String message) {
    CompletableFuture<CounselorTrendReportVO> future = pendingMap.get(requestId);
    if (future != null) {
      future.completeExceptionally(new RuntimeException(message));
    }
  }
}