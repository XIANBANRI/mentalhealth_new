package com.sl.mentalhealth.dto;

import lombok.Data;

@Data
public class CounselorProfileRequest {

  private String account;

  public CounselorProfileRequest() {
  }

  public CounselorProfileRequest(String account) {
    this.account = account;
  }

}