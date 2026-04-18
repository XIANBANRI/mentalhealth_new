package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.CounselorTrendReportQueryRequest;
import com.sl.mentalhealth.service.CounselorTrendReportGatewayService;
import com.sl.mentalhealth.vo.CounselorTrendReportVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorTrendReportControllerTest {

  @Mock
  private CounselorTrendReportGatewayService counselorTrendReportGatewayService;

  @InjectMocks
  private CounselorTrendReportController controller;

  @Test
  void query_success() {
    CounselorTrendReportQueryRequest request = mock(CounselorTrendReportQueryRequest.class);
    CounselorTrendReportVO vo = mock(CounselorTrendReportVO.class);

    when(counselorTrendReportGatewayService.queryTrendReport(request)).thenReturn(vo);

    Result<CounselorTrendReportVO> result = controller.query(request);

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(vo, result.getData());
  }
}