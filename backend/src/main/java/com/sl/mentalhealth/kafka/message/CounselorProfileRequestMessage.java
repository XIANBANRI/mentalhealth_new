package com.sl.mentalhealth.kafka.message;

import java.io.Serializable;

public class CounselorProfileRequestMessage implements Serializable {

  public static final String ACTION_QUERY_PROFILE = "QUERY_PROFILE";
  public static final String ACTION_UPDATE_AVATAR = "UPDATE_AVATAR";

  private String correlationId;
  private String action;
  private String account;
  private String avatarUrl;

  public CounselorProfileRequestMessage() {
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }
}