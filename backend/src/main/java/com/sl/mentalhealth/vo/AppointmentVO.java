package com.sl.mentalhealth.vo;

import lombok.Data;

@Data
public class AppointmentVO {

  private Long id;
  private String appointmentNo;

  private String studentAccount;
  private String studentName;
  private String college;
  private String className;

  private String teacherAccount;
  private String teacherName;
  private String officeLocation;
  private String teacherPhone;

  private Long scheduleId;
  private String appointmentDate;
  private String startTime;
  private String endTime;

  private String purpose;
  private String remark;
  private String teacherReply;
  private String rejectReason;
  private String status;

  private String createdAt;
  private String approvedAt;
  private String cancelledAt;
  private String completedAt;
}