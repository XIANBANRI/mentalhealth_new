package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.vo.TeacherScheduleVO;
import java.util.List;
import lombok.Data;

@Data
public class TeacherScheduleResponseMessage {
  private String requestId;
  private boolean success;
  private String message;
  private List<TeacherScheduleVO> list;
  private TeacherScheduleVO data;
}