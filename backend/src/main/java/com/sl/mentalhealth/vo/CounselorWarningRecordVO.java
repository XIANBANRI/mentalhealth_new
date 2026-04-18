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
public class CounselorWarningRecordVO {

  private Long id;
  private String scaleCode;
  private String scaleName;
  private Integer rawScore;
  private String resultLevel;
  private String resultSummary;
  private String suggestion;
  private LocalDateTime submittedAt;
}