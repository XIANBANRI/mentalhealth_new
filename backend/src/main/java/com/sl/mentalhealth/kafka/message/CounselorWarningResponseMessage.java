package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.vo.CounselorWarningDetailVO;
import com.sl.mentalhealth.vo.CounselorWarningPageVO;
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
public class CounselorWarningResponseMessage implements Serializable {

  private String requestId;
  private Boolean success;
  private String message;

  private CounselorWarningPageVO pageData;
  private CounselorWarningDetailVO detailData;
  private List<String> classOptions;
}