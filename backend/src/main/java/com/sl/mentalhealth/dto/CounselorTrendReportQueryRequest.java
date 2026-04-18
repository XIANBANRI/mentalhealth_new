package com.sl.mentalhealth.dto;

import lombok.Data;

@Data
public class CounselorTrendReportQueryRequest {

  /**
   * 辅导员账号
   */
  private String counselorAccount;

  /**
   * 当前选中的学期，可为空，默认第1学期
   */
  private String semester;
}