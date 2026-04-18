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
public class CounselorStudentRequestMessage implements Serializable {

  private String requestId;

  private String action;

  private String counselorAccount;
  private String studentId;
  private String className;
  private String keyword;
  private Integer pageNum;
  private Integer pageSize;
}