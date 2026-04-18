package com.sl.mentalhealth.vo;

import java.util.List;

public class AdminCounselorDetailVO {

  private String account;
  private String name;
  private String college;
  private String grade;
  private String phone;
  private String avatarUrl;
  private List<String> classList;

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
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

  public List<String> getClassList() {
    return classList;
  }

  public void setClassList(List<String> classList) {
    this.classList = classList;
  }
}