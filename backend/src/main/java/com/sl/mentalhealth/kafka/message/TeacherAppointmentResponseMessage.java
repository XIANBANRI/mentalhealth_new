package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.vo.TeacherAppointmentVO;
import com.sl.mentalhealth.vo.TeacherAssessmentRecordVO;
import java.util.List;
import lombok.Data;

@Data
public class TeacherAppointmentResponseMessage {
  private String requestId;
  private boolean success;
  private String message;
  private List<TeacherAppointmentVO> appointmentList;
  private TeacherAppointmentVO appointmentData;
  private List<TeacherAssessmentRecordVO> assessmentRecordList;
}