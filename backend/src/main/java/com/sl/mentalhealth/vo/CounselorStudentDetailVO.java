package com.sl.mentalhealth.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselorStudentDetailVO {

  /**
   * student表中除 password、avatarUrl 外的全部信息
   */
  private String studentId;
  private String name;
  private String college;
  private String className;
  private String grade;
  private String phone;

  /**
   * 学生学期测评汇总记录
   */
  private List<CounselorStudentAssessmentSummaryVO> assessmentSummaries;
}