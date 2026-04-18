package com.sl.mentalhealth.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselorStudentAssessmentSummaryVO {

  private Long id;
  private String studentId;
  private String semester;
  private Integer testedCount;
  private String scoreSummary;
  private String semesterLevel;
  private LocalDateTime lastTestedAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}