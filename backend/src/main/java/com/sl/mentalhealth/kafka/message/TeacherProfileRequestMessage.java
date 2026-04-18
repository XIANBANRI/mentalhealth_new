package com.sl.mentalhealth.kafka.message;

import lombok.Data;

@Data
public class TeacherProfileRequestMessage {

  public static final String ACTION_QUERY_PROFILE = "QUERY_PROFILE";
  public static final String ACTION_UPDATE_AVATAR = "UPDATE_AVATAR";

  private String requestId;
  private String action;
  private String teacherAccount;
  private String avatarUrl;
}