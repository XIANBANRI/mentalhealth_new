package com.sl.mentalhealth.service;

import com.sl.mentalhealth.dto.CounselorStudentQueryRequest;
import com.sl.mentalhealth.kafka.CounselorStudentRequestProducer;
import com.sl.mentalhealth.kafka.message.CounselorStudentRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorStudentResponseMessage;
import com.sl.mentalhealth.vo.CounselorStudentDetailVO;
import com.sl.mentalhealth.vo.CounselorStudentPageVO;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CounselorStudentGatewayService {

  private final CounselorStudentRequestProducer counselorStudentRequestProducer;
  private final PendingCounselorStudentService pendingCounselorStudentService;

  public List<String> listManagedClasses(String counselorAccount) {
    String requestId = UUID.randomUUID().toString();

    CounselorStudentRequestMessage message = CounselorStudentRequestMessage.builder()
        .requestId(requestId)
        .action("LIST_CLASSES")
        .counselorAccount(counselorAccount)
        .build();

    CounselorStudentResponseMessage response = sendAndWait(message);
    if (!Boolean.TRUE.equals(response.getSuccess())) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getClassOptions();
  }

  public CounselorStudentPageVO listStudents(CounselorStudentQueryRequest request) {
    String requestId = UUID.randomUUID().toString();

    CounselorStudentRequestMessage message = CounselorStudentRequestMessage.builder()
        .requestId(requestId)
        .action("LIST")
        .counselorAccount(request.getCounselorAccount())
        .className(request.getClassName())
        .keyword(request.getKeyword())
        .pageNum(request.getPageNum())
        .pageSize(request.getPageSize())
        .build();

    CounselorStudentResponseMessage response = sendAndWait(message);
    if (!Boolean.TRUE.equals(response.getSuccess())) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getPageData();
  }

  public CounselorStudentDetailVO getStudentDetail(String counselorAccount, String studentId) {
    String requestId = UUID.randomUUID().toString();

    CounselorStudentRequestMessage message = CounselorStudentRequestMessage.builder()
        .requestId(requestId)
        .action("DETAIL")
        .counselorAccount(counselorAccount)
        .studentId(studentId)
        .build();

    CounselorStudentResponseMessage response = sendAndWait(message);
    if (!Boolean.TRUE.equals(response.getSuccess())) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getDetailData();
  }

  private CounselorStudentResponseMessage sendAndWait(CounselorStudentRequestMessage message) {
    CompletableFuture<CounselorStudentResponseMessage> future =
        pendingCounselorStudentService.create(message.getRequestId());

    try {
      counselorStudentRequestProducer.send(message);
      return future.get(10, TimeUnit.SECONDS);
    } catch (Exception e) {
      pendingCounselorStudentService.remove(message.getRequestId());
      throw new RuntimeException("请求处理超时或失败：" + e.getMessage(), e);
    }
  }
}