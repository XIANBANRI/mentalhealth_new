package com.sl.mentalhealth.service;

import com.sl.mentalhealth.dto.CounselorTrendReportQueryRequest;
import com.sl.mentalhealth.kafka.CounselorTrendReportRequestProducer;
import com.sl.mentalhealth.kafka.message.CounselorTrendReportRequestMessage;
import com.sl.mentalhealth.vo.CounselorTrendReportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CounselorTrendReportGatewayService {

  private final CounselorTrendReportRequestProducer counselorTrendReportRequestProducer;
  private final PendingCounselorTrendReportService pendingCounselorTrendReportService;

  public CounselorTrendReportVO queryTrendReport(CounselorTrendReportQueryRequest request) {
    String requestId = UUID.randomUUID().toString();

    pendingCounselorTrendReportService.create(requestId);

    CounselorTrendReportRequestMessage message = new CounselorTrendReportRequestMessage();
    message.setRequestId(requestId);
    message.setCounselorAccount(request.getCounselorAccount());
    message.setSemester(request.getSemester());

    counselorTrendReportRequestProducer.send(message);

    return pendingCounselorTrendReportService.await(requestId, 10, TimeUnit.SECONDS);
  }
}