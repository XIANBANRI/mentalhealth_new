package com.sl.mentalhealth.kafka.message;

public class StudentProfileRequestMessage {

  public static final String ACTION_QUERY_PROFILE = "QUERY_PROFILE";
  public static final String ACTION_UPDATE_AVATAR = "UPDATE_AVATAR";

  private String requestId;
  private String action;
  private String studentId;
  private String avatarUrl;

  public StudentProfileRequestMessage() {
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }
}