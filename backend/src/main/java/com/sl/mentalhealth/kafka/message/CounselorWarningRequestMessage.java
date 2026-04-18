package com.sl.mentalhealth.kafka.message;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselorWarningRequestMessage implements Serializable {

  private String requestId;

  /**
   * LIST / DETAIL / LIST_CLASSES
   */
  private String action;

  private String counselorAccount;
  private String semester;
  private String className;
  private String studentId;
  private Integer pageNum;
  private Integer pageSize;
}