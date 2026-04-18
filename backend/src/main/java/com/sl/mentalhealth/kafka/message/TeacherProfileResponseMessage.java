package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.vo.TeacherProfileResponseVO;
import lombok.Data;

@Data
public class TeacherProfileResponseMessage {

  private String requestId;
  private boolean success;
  private String message;
  private TeacherProfileResponseVO data;
}