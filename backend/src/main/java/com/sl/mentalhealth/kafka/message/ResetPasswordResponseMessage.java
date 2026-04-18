package com.sl.mentalhealth.kafka.message;

public class ResetPasswordResponseMessage {

  private String requestId;
  private Boolean success;
  private String message;

  public ResetPasswordResponseMessage() {
  }

  public ResetPasswordResponseMessage(String requestId, Boolean success, String message) {
    this.requestId = requestId;
    this.success = success;
    this.message = message;
  }

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
}