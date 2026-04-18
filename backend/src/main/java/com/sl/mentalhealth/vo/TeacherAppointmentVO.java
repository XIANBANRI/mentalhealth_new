package com.sl.mentalhealth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherAppointmentVO {
  private Long id;
  private String appointmentNo;
  private String studentId;
  private String studentName;
  private String teacherAccount;
  private Long scheduleId;
  private String appointmentDate;
  private String startTime;
  private String endTime;
  private String purpose;
  private String remark;
  private String offlineRecord;
  private String rejectReason;
  private Boolean recordCompleted;
  private String status;
  private String createdAt;
  private String approvedAt;
  private String completedAt;
}