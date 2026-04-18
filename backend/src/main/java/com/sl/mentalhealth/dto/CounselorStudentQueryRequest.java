package com.sl.mentalhealth.dto;

import lombok.Data;

@Data
public class CounselorStudentQueryRequest {


  private String counselorAccount;

  private String className;

  private String keyword;

  private Integer pageNum = 1;

  private Integer pageSize = 10;
}