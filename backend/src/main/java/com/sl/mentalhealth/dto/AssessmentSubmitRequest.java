package com.sl.mentalhealth.dto;

import java.util.List;
import lombok.Data;

@Data
public class AssessmentSubmitRequest {

  private String studentId;
  private String semester;
  private Long scaleId;
  private Long versionId;
  private List<AssessmentSubmitAnswer> answers;

}
