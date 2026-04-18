package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.dto.CounselorTrendReportQueryRequest;
import com.sl.mentalhealth.kafka.message.CounselorTrendReportRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorTrendReportResponseMessage;
import com.sl.mentalhealth.service.LocalCounselorTrendReportService;
import com.sl.mentalhealth.vo.CounselorTrendReportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounselorTrendReportRequestConsumer {

  private final LocalCounselorTrendReportService localCounselorTrendReportService;
  private final CounselorTrendReportResponseProducer counselorTrendReportResponseProducer;

  @KafkaListener(
      topics = KafkaTopics.COUNSELOR_TREND_REPORT_REQUEST,
      groupId = "mentalhealth-counselor-trend-request-group",
      containerFactory = "counselorTrendReportRequestKafkaListenerContainerFactory"
  )
  public void consume(CounselorTrendReportRequestMessage message) {
    CounselorTrendReportResponseMessage response = new CounselorTrendReportResponseMessage();
    response.setRequestId(message.getRequestId());

    try {
      CounselorTrendReportQueryRequest request = new CounselorTrendReportQueryRequest();
      request.setCounselorAccount(message.getCounselorAccount());
      request.setSemester(message.getSemester());

      CounselorTrendReportVO data = localCounselorTrendReportService.queryTrendReport(request);

      response.setSuccess(true);
      response.setMessage("查询成功");
      response.setData(data);
    } catch (Exception e) {
      response.setSuccess(false);
      response.setMessage(e.getMessage());
      response.setData(null);
    }

    counselorTrendReportResponseProducer.send(response);
  }
}