package com.sl.mentalhealth.vo;

import lombok.Data;

@Data
public class AvailableAppointmentVO {

  private Long scheduleId;
  private String teacherAccount;
  private String teacherName;
  private String officeLocation;
  private String phone;

  private Integer weekDay;
  private String startTime;
  private String endTime;

  private Integer maxAppointments;
  private Integer usedAppointments;
  private Integer remainingAppointments;

  private String remark;
}