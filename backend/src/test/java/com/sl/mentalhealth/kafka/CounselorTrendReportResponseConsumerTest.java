package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorTrendReportResponseMessage;
import com.sl.mentalhealth.service.PendingCounselorTrendReportService;
import com.sl.mentalhealth.vo.CounselorTrendReportVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorTrendReportResponseConsumerTest {

  @Mock
  private PendingCounselorTrendReportService pendingCounselorTrendReportService;

  @InjectMocks
  private CounselorTrendReportResponseConsumer consumer;

  @Test
  void consume_success() {
    CounselorTrendReportResponseMessage message = mock(CounselorTrendReportResponseMessage.class);
    CounselorTrendReportVO data = mock(CounselorTrendReportVO.class);

    when(message.getSuccess()).thenReturn(true);
    when(message.getRequestId()).thenReturn("req-001");
    when(message.getData()).thenReturn(data);

    consumer.consume(message);

    verify(pendingCounselorTrendReportService, times(1))
        .complete("req-001", data);
    verify(pendingCounselorTrendReportService, never())
        .completeExceptionally(anyString(), anyString());
  }

  @Test
  void consume_fail_useMessage() {
    CounselorTrendReportResponseMessage message = mock(CounselorTrendReportResponseMessage.class);

    when(message.getSuccess()).thenReturn(false);
    when(message.getRequestId()).thenReturn("req-002");
    when(message.getMessage()).thenReturn("查询失败");

    consumer.consume(message);

    verify(pendingCounselorTrendReportService, times(1))
        .completeExceptionally("req-002", "查询失败");
    verify(pendingCounselorTrendReportService, never())
        .complete(anyString(), any());
  }

  @Test
  void consume_fail_useDefaultMessageWhenNull() {
    CounselorTrendReportResponseMessage message = mock(CounselorTrendReportResponseMessage.class);

    when(message.getSuccess()).thenReturn(false);
    when(message.getRequestId()).thenReturn("req-003");
    when(message.getMessage()).thenReturn(null);

    consumer.consume(message);

    verify(pendingCounselorTrendReportService, times(1))
        .completeExceptionally("req-003", "趋势报告查询失败");
    verify(pendingCounselorTrendReportService, never())
        .complete(anyString(), any());
  }
}