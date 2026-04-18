package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.vo.CounselorProfileResponseVO;
import java.io.Serializable;

public class CounselorProfileResponseMessage implements Serializable {

  private String correlationId;
  private boolean success;
  private String message;
  private CounselorProfileResponseVO data;

  public CounselorProfileResponseMessage() {
  }

  public CounselorProfileResponseMessage(String correlationId, boolean success, String message,
      CounselorProfileResponseVO data) {
    this.correlationId = correlationId;
    this.success = success;
    this.message = message;
    this.data = data;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public CounselorProfileResponseVO getData() {
    return data;
  }

  public void setData(CounselorProfileResponseVO data) {
    this.data = data;
  }
}