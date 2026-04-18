package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.vo.CounselorTrendReportVO;
import lombok.Data;

@Data
public class CounselorTrendReportResponseMessage {

  private String requestId;

  private Boolean success;

  private String message;

  private CounselorTrendReportVO data;
}