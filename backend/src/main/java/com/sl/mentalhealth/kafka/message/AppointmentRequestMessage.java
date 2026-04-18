package com.sl.mentalhealth.kafka.message;

import lombok.Data;

@Data
public class AppointmentRequestMessage {

  private String operation;
  private String requestId;

  // 学生端
  private String studentId;
  private Long scheduleId;
  private String appointmentDate;
  private String purpose;
  private String remark;
  private Long appointmentId;
  private String date;

  // 老师端
  private String teacherAccount;
  private String status;
  private String teacherReply;
  private String rejectReason;
}