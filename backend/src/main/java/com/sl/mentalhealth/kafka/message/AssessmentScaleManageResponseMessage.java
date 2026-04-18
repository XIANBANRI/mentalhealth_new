package com.sl.mentalhealth.kafka.message;

import lombok.Data;

@Data
public class AssessmentScaleManageResponseMessage {

  private String requestId;
  private Boolean success;
  private String message;
  private Object data;

  public static AssessmentScaleManageResponseMessage ok(String requestId, String message, Object data) {
    AssessmentScaleManageResponseMessage response = new AssessmentScaleManageResponseMessage();
    response.setRequestId(requestId);
    response.setSuccess(true);
    response.setMessage(message);
    response.setData(data);
    return response;
  }

  public static AssessmentScaleManageResponseMessage fail(String requestId, String message) {
    AssessmentScaleManageResponseMessage response = new AssessmentScaleManageResponseMessage();
    response.setRequestId(requestId);
    response.setSuccess(false);
    response.setMessage(message);
    response.setData(null);
    return response;
  }
}