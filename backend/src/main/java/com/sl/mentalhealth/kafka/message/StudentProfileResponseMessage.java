package com.sl.mentalhealth.kafka.message;

public class StudentProfileResponseMessage {

  private String requestId;
  private Boolean success;
  private String message;

  private String studentId;
  private String name;
  private String className;
  private String college;
  private String grade;
  private String phone;
  private String avatarUrl;
  private String counselorName;
  private String counselorPhone;

  public StudentProfileResponseMessage() {
  }

  public StudentProfileResponseMessage(String requestId, Boolean success, String message,
      String studentId, String name, String className, String college, String grade,
      String phone, String avatarUrl, String counselorName, String counselorPhone) {
    this.requestId = requestId;
    this.success = success;
    this.message = message;
    this.studentId = studentId;
    this.name = name;
    this.className = className;
    this.college = college;
    this.grade = grade;
    this.phone = phone;
    this.avatarUrl = avatarUrl;
    this.counselorName = counselorName;
    this.counselorPhone = counselorPhone;
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

  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getCollege() {
    return college;
  }

  public void setCollege(String college) {
    this.college = college;
  }

  public String getGrade() {
    return grade;
  }

  public void setGrade(String grade) {
    this.grade = grade;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public String getCounselorName() {
    return counselorName;
  }

  public void setCounselorName(String counselorName) {
    this.counselorName = counselorName;
  }

  public String getCounselorPhone() {
    return counselorPhone;
  }

  public void setCounselorPhone(String counselorPhone) {
    this.counselorPhone = counselorPhone;
  }
}