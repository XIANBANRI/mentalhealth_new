package com.sl.mentalhealth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CounselorTrendLineVO {

  /**
   * 学期
   */
  private String semester;

  /**
   * 危险总人数
   */
  private Long dangerCount;
}