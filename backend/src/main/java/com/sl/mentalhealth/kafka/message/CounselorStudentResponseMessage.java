package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.vo.CounselorStudentDetailVO;
import com.sl.mentalhealth.vo.CounselorStudentPageVO;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselorStudentResponseMessage implements Serializable {

  private String requestId;
  private Boolean success;
  private String message;

  /**
   * 列表数据
   */
  private CounselorStudentPageVO pageData;

  /**
   * 详情数据
   */
  private CounselorStudentDetailVO detailData;

  /**
   * 当前辅导员所管理的班级列表
   */
  private List<String> classOptions;
}