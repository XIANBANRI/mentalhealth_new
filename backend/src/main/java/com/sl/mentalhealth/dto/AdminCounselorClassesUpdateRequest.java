package com.sl.mentalhealth.dto;

import java.util.List;

public class AdminCounselorClassesUpdateRequest {

  private String account;
  private List<String> classList;

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public List<String> getClassList() {
    return classList;
  }

  public void setClassList(List<String> classList) {
    this.classList = classList;
  }
}