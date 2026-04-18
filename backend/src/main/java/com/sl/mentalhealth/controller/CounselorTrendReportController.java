package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.config.UserContext;
import com.sl.mentalhealth.dto.CounselorTrendReportQueryRequest;
import com.sl.mentalhealth.service.CounselorTrendReportGatewayService;
import com.sl.mentalhealth.vo.CounselorTrendReportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/counselor/trend-report")
@RequiredArgsConstructor
public class CounselorTrendReportController {

  private final CounselorTrendReportGatewayService counselorTrendReportGatewayService;

  @PostMapping("/query")
  public Result<CounselorTrendReportVO> query(@RequestBody CounselorTrendReportQueryRequest request) {
    if (request == null) {
      request = new CounselorTrendReportQueryRequest();
    }
    request.setCounselorAccount(UserContext.getUsername());

    CounselorTrendReportVO data = counselorTrendReportGatewayService.queryTrendReport(request);
    return Result.success("查询成功", data);
  }
}