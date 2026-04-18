package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.dto.CounselorTrendReportQueryRequest;
import com.sl.mentalhealth.kafka.CounselorTrendReportRequestProducer;
import com.sl.mentalhealth.kafka.message.CounselorTrendReportRequestMessage;
import com.sl.mentalhealth.vo.CounselorTrendReportVO;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CounselorTrendReportGatewayServiceTest {

    @Mock
    private CounselorTrendReportRequestProducer counselorTrendReportRequestProducer;

    @Mock
    private PendingCounselorTrendReportService pendingCounselorTrendReportService;

    @InjectMocks
    private CounselorTrendReportGatewayService service;

    @Test
    void queryTrendReport_success() {
        CounselorTrendReportQueryRequest request = new CounselorTrendReportQueryRequest();
        request.setCounselorAccount("c001");
        request.setSemester("2025-2026-1");

        CounselorTrendReportVO vo = new CounselorTrendReportVO();
        when(pendingCounselorTrendReportService.await(anyString(), anyLong(), eq(TimeUnit.SECONDS)))
                .thenReturn(vo);

        CounselorTrendReportVO result = service.queryTrendReport(request);

        assertSame(vo, result);

        ArgumentCaptor<CounselorTrendReportRequestMessage> messageCaptor =
                ArgumentCaptor.forClass(CounselorTrendReportRequestMessage.class);
        verify(counselorTrendReportRequestProducer).send(messageCaptor.capture());
        CounselorTrendReportRequestMessage sent = messageCaptor.getValue();
        assertSame(result, vo);
        verify(pendingCounselorTrendReportService).create(sent.getRequestId());
        verify(pendingCounselorTrendReportService).await(sent.getRequestId(), 10, TimeUnit.SECONDS);
    }
}
