package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.dto.CounselorTrendReportQueryRequest;
import com.sl.mentalhealth.kafka.message.CounselorTrendReportRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorTrendReportResponseMessage;
import com.sl.mentalhealth.service.LocalCounselorTrendReportService;
import com.sl.mentalhealth.vo.CounselorTrendReportVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorTrendReportRequestConsumerTest {

  @Mock
  private LocalCounselorTrendReportService localCounselorTrendReportService;

  @Mock
  private CounselorTrendReportResponseProducer counselorTrendReportResponseProducer;

  @InjectMocks
  private CounselorTrendReportRequestConsumer consumer;

  @Test
  void consume_success() {
    CounselorTrendReportRequestMessage message = mock(CounselorTrendReportRequestMessage.class);
    CounselorTrendReportVO data = mock(CounselorTrendReportVO.class);

    when(message.getRequestId()).thenReturn("req-001");
    when(message.getCounselorAccount()).thenReturn("c001");
    when(message.getSemester()).thenReturn("2025-2026-1");
    when(localCounselorTrendReportService.queryTrendReport(any(CounselorTrendReportQueryRequest.class)))
        .thenReturn(data);

    consumer.consume(message);

    ArgumentCaptor<CounselorTrendReportQueryRequest> requestCaptor =
        ArgumentCaptor.forClass(CounselorTrendReportQueryRequest.class);
    verify(localCounselorTrendReportService, times(1))
        .queryTrendReport(requestCaptor.capture());

    CounselorTrendReportQueryRequest actualRequest = requestCaptor.getValue();
    assertEquals("c001", actualRequest.getCounselorAccount());
    assertEquals("2025-2026-1", actualRequest.getSemester());

    CounselorTrendReportResponseMessage response = captureResponse();
    assertEquals("req-001", response.getRequestId());
    assertTrue(response.getSuccess());
    assertEquals("查询成功", response.getMessage());
    assertSame(data, response.getData());
  }

  @Test
  void consume_exception() {
    CounselorTrendReportRequestMessage message = mock(CounselorTrendReportRequestMessage.class);

    when(message.getRequestId()).thenReturn("req-002");
    when(message.getCounselorAccount()).thenReturn("c001");
    when(message.getSemester()).thenReturn("2025-2026-1");
    when(localCounselorTrendReportService.queryTrendReport(any(CounselorTrendReportQueryRequest.class)))
        .thenThrow(new RuntimeException("查询异常"));

    consumer.consume(message);

    CounselorTrendReportResponseMessage response = captureResponse();
    assertEquals("req-002", response.getRequestId());
    assertFalse(response.getSuccess());
    assertEquals("查询异常", response.getMessage());
    assertNull(response.getData());
  }

  private CounselorTrendReportResponseMessage captureResponse() {
    ArgumentCaptor<CounselorTrendReportResponseMessage> captor =
        ArgumentCaptor.forClass(CounselorTrendReportResponseMessage.class);
    verify(counselorTrendReportResponseProducer, atLeastOnce()).send(captor.capture());
    return captor.getValue();
  }
}