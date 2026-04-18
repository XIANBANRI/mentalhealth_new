package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.dto.TeacherScheduleDeleteRequest;
import com.sl.mentalhealth.dto.TeacherScheduleQueryRequest;
import com.sl.mentalhealth.dto.TeacherScheduleSaveRequest;
import lombok.Data;

@Data
public class TeacherScheduleRequestMessage {
  private String requestId;
  /**
   * QUERY / ADD / UPDATE / DELETE
   */
  private String action;
  private TeacherScheduleQueryRequest queryRequest;
  private TeacherScheduleSaveRequest saveRequest;
  private TeacherScheduleDeleteRequest deleteRequest;
}