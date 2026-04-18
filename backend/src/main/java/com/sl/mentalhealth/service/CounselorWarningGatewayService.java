package com.sl.mentalhealth.service;

import com.sl.mentalhealth.dto.CounselorWarningQueryRequest;
import com.sl.mentalhealth.kafka.CounselorWarningRequestProducer;
import com.sl.mentalhealth.kafka.message.CounselorWarningRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorWarningResponseMessage;
import com.sl.mentalhealth.vo.CounselorWarningDetailVO;
import com.sl.mentalhealth.vo.CounselorWarningPageVO;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CounselorWarningGatewayService {

  private final CounselorWarningRequestProducer counselorWarningRequestProducer;
  private final PendingCounselorWarningService pendingCounselorWarningService;

  public List<String> listManagedClasses(String counselorAccount) {
    String requestId = UUID.randomUUID().toString();

    CounselorWarningRequestMessage message = CounselorWarningRequestMessage.builder()
        .requestId(requestId)
        .action("LIST_CLASSES")
        .counselorAccount(counselorAccount)
        .build();

    CounselorWarningResponseMessage response = sendAndWait(message);
    if (!Boolean.TRUE.equals(response.getSuccess())) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getClassOptions();
  }

  public CounselorWarningPageVO listDangerousStudents(CounselorWarningQueryRequest request) {
    String requestId = UUID.randomUUID().toString();

    CounselorWarningRequestMessage message = CounselorWarningRequestMessage.builder()
        .requestId(requestId)
        .action("LIST")
        .counselorAccount(request.getCounselorAccount())
        .semester(request.getSemester())
        .className(request.getClassName())
        .pageNum(request.getPageNum())
        .pageSize(request.getPageSize())
        .build();

    CounselorWarningResponseMessage response = sendAndWait(message);
    if (!Boolean.TRUE.equals(response.getSuccess())) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getPageData();
  }

  public CounselorWarningDetailVO getDangerousStudentDetail(String counselorAccount,
      String studentId,
      String semester) {
    String requestId = UUID.randomUUID().toString();

    CounselorWarningRequestMessage message = CounselorWarningRequestMessage.builder()
        .requestId(requestId)
        .action("DETAIL")
        .counselorAccount(counselorAccount)
        .studentId(studentId)
        .semester(semester)
        .build();

    CounselorWarningResponseMessage response = sendAndWait(message);
    if (!Boolean.TRUE.equals(response.getSuccess())) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getDetailData();
  }

  private CounselorWarningResponseMessage sendAndWait(CounselorWarningRequestMessage message) {
    CompletableFuture<CounselorWarningResponseMessage> future =
        pendingCounselorWarningService.create(message.getRequestId());

    try {
      counselorWarningRequestProducer.send(message);
      return future.get(10, TimeUnit.SECONDS);
    } catch (Exception e) {
      pendingCounselorWarningService.remove(message.getRequestId());
      throw new RuntimeException("请求处理超时或失败：" + e.getMessage(), e);
    }
  }
}