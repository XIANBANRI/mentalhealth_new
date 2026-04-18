package com.sl.mentalhealth.vo;

public class CounselorProfileResponseVO {

  private String counselorId;
  private String name;
  private String college;
  private String grade;
  private String phone;
  private String avatarUrl;

  public CounselorProfileResponseVO() {
  }

  public String getCounselorId() {
    return counselorId;
  }

  public void setCounselorId(String counselorId) {
    this.counselorId = counselorId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
}