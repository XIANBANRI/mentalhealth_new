package com.sl.mentalhealth.vo;

import lombok.Data;

import java.util.List;

@Data
public class CounselorTrendReportVO {

  /**
   * 学期下拉框选项
   */
  private List<String> semesterOptions;

  /**
   * 当前选中学期
   */
  private String selectedSemester;

  /**
   * 柱状图：当前学期各班危险人数
   */
  private List<CounselorTrendBarVO> barChart;

  /**
   * 折线图：8个学期危险总人数
   */
  private List<CounselorTrendLineVO> lineChart;
}