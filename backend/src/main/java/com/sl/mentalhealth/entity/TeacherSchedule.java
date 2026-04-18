package com.sl.mentalhealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Data;

@Data
@TableName("teacher_schedule")
public class TeacherSchedule {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  @TableField("teacher_account")
  private String teacherAccount;

  @TableField("week_day")
  private Integer weekDay;

  @TableField("start_time")
  private LocalTime startTime;

  @TableField("end_time")
  private LocalTime endTime;

  @TableField("max_appointments")
  private Integer maxAppointments;

  @TableField("remark")
  private String remark;

  @TableField("status")
  private Integer status;

  @TableField("created_at")
  private LocalDateTime createdAt;

  @TableField("updated_at")
  private LocalDateTime updatedAt;
}