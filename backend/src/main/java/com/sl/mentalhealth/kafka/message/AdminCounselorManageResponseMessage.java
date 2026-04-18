package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.vo.AdminCounselorDetailVO;
import com.sl.mentalhealth.vo.AdminCounselorPageVO;

public class AdminCounselorManageResponseMessage {

  private String requestId;
  private boolean success;
  private String message;
  private AdminCounselorPageVO page;
  private AdminCounselorDetailVO detail;

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
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

  public AdminCounselorPageVO getPage() {
    return page;
  }

  public void setPage(AdminCounselorPageVO page) {
    this.page = page;
  }

  public AdminCounselorDetailVO getDetail() {
    return detail;
  }

  public void setDetail(AdminCounselorDetailVO detail) {
    this.detail = detail;
  }
}