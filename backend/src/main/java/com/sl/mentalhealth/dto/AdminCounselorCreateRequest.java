package com.sl.mentalhealth.dto;

import java.util.List;

public class AdminCounselorCreateRequest {

  private String account;
  private String name;
  private String password;
  private String college;
  private String grade;
  private String phone;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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

  public List<String> getClassList() {
    return classList;
  }

  public void setClassList(List<String> classList) {
    this.classList = classList;
  }
}