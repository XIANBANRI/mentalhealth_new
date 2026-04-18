package com.sl.mentalhealth.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherAssessmentRecordVO {

  private String studentId;
  private String studentName;
  private String college;
  private String className;

  private String semester;
  private Integer testedCount;
  private String scoreSummary;
  private String semesterLevel;

  private List<DetailItem> details = new ArrayList<>();

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DetailItem {
    private String scaleCode;
    private String scaleName;
    private Integer rawScore;
    private String resultLevel;
    private String resultSummary;
    private String suggestion;
    private String submittedAt;
  }
}