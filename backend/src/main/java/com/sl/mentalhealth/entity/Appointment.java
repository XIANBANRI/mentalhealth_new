package com.sl.mentalhealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Data;

@Data
@TableName("appointment")
public class Appointment {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  @TableField("appointment_no")
  private String appointmentNo;

  @TableField("student_account")
  private String studentAccount;

  @TableField("teacher_account")
  private String teacherAccount;

  @TableField("schedule_id")
  private Long scheduleId;

  @TableField("appointment_date")
  private LocalDate appointmentDate;

  @TableField("start_time")
  private LocalTime startTime;

  @TableField("end_time")
  private LocalTime endTime;

  @TableField("purpose")
  private String purpose;

  @TableField("remark")
  private String remark;

  @TableField("teacher_reply")
  private String teacherReply;

  @TableField("reject_reason")
  private String rejectReason;

  @TableField("status")
  private String status;

  @TableField("created_at")
  private LocalDateTime createdAt;

  @TableField("updated_at")
  private LocalDateTime updatedAt;

  @TableField("approved_at")
  private LocalDateTime approvedAt;

  @TableField("cancelled_at")
  private LocalDateTime cancelledAt;

  @TableField("completed_at")
  private LocalDateTime completedAt;
}