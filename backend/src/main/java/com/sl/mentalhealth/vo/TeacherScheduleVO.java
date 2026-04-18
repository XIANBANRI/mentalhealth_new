package com.sl.mentalhealth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherScheduleVO {
  private Long id;
  private String teacherAccount;
  private Integer weekDay;
  private String startTime;
  private String endTime;
  private Integer maxAppointments;
  private String remark;
}