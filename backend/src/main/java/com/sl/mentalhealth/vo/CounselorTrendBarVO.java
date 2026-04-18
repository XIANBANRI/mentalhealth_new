package com.sl.mentalhealth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CounselorTrendBarVO {

  /**
   * 班级名称
   */
  private String className;

  /**
   * 该班危险人数
   */
  private Long dangerCount;
}