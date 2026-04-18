package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.vo.AssessmentRecordVO;
import com.sl.mentalhealth.vo.AssessmentScaleDetailVO;
import com.sl.mentalhealth.vo.AssessmentScaleVO;
import com.sl.mentalhealth.vo.AssessmentSubmitResultVO;
import java.util.List;

public class AssessmentResponseMessage {

  private String requestId;
  private Boolean success;
  private String message;

  private List<AssessmentScaleVO> scales;
  private AssessmentScaleDetailVO detail;
  private AssessmentSubmitResultVO submitResult;
  private List<AssessmentRecordVO> records;

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<AssessmentScaleVO> getScales() {
    return scales;
  }

  public void setScales(List<AssessmentScaleVO> scales) {
    this.scales = scales;
  }

  public AssessmentScaleDetailVO getDetail() {
    return detail;
  }

  public void setDetail(AssessmentScaleDetailVO detail) {
    this.detail = detail;
  }

  public AssessmentSubmitResultVO getSubmitResult() {
    return submitResult;
  }

  public void setSubmitResult(AssessmentSubmitResultVO submitResult) {
    this.submitResult = submitResult;
  }

  public List<AssessmentRecordVO> getRecords() {
    return records;
  }

  public void setRecords(List<AssessmentRecordVO> records) {
    this.records = records;
  }
}
