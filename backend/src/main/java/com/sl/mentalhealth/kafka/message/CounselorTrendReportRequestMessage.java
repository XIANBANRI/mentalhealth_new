package com.sl.mentalhealth.kafka.message;

import lombok.Data;

@Data
public class CounselorTrendReportRequestMessage {

  private String requestId;

  private String counselorAccount;

  private String semester;
}