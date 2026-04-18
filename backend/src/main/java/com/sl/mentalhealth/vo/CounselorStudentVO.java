package com.sl.mentalhealth.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselorStudentVO {

  private String studentId;
  private String name;
  private String college;
  private String className;
  private String grade;
  private String phone;
}