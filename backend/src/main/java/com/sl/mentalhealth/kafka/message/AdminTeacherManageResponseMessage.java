package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.vo.AdminTeacherPageVO;
import com.sl.mentalhealth.vo.AdminTeacherVO;

public class AdminTeacherManageResponseMessage {

  private String requestId;
  private boolean success;
  private String message;
  private AdminTeacherVO teacher;
  private AdminTeacherPageVO page;

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

  public AdminTeacherVO getTeacher() {
    return teacher;
  }

  public void setTeacher(AdminTeacherVO teacher) {
    this.teacher = teacher;
  }

  public AdminTeacherPageVO getPage() {
    return page;
  }

  public void setPage(AdminTeacherPageVO page) {
    this.page = page;
  }
}