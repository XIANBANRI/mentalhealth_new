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
public class CounselorWarningDetailVO {

  private String studentId;
  private String name;
  private String className;
  private String phone;
  private String semester;
  private String college;
  private List<CounselorWarningRecordVO> records;
}