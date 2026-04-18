package com.sl.mentalhealth.dto;

import lombok.Data;

@Data
public class CounselorWarningQueryRequest {

  /**
   * 辅导员账号
   */
  private String counselorAccount;

  /**
   * 学期，默认第1学期
   */
  private String semester = "第1学期";

  /**
   * 班级；空字符串表示全部
   */
  private String className = "";

  /**
   * 页码
   */
  private Integer pageNum = 1;

  /**
   * 每页大小
   */
  private Integer pageSize = 10;
}