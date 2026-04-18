package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.dto.AdminTeacherCreateRequest;
import com.sl.mentalhealth.dto.AdminTeacherQueryRequest;
import com.sl.mentalhealth.dto.AdminTeacherUpdateRequest;

public class AdminTeacherManageRequestMessage {

  public static final String ACTION_QUERY_PAGE = "QUERY_PAGE";
  public static final String ACTION_DETAIL = "DETAIL";
  public static final String ACTION_CREATE = "CREATE";
  public static final String ACTION_UPDATE = "UPDATE";

  private String requestId;
  private String action;
  private String account;
  private AdminTeacherQueryRequest queryRequest;
  private AdminTeacherCreateRequest createRequest;
  private AdminTeacherUpdateRequest updateRequest;

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

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public AdminTeacherQueryRequest getQueryRequest() {
    return queryRequest;
  }

  public void setQueryRequest(AdminTeacherQueryRequest queryRequest) {
    this.queryRequest = queryRequest;
  }

  public AdminTeacherCreateRequest getCreateRequest() {
    return createRequest;
  }

  public void setCreateRequest(AdminTeacherCreateRequest createRequest) {
    this.createRequest = createRequest;
  }

  public AdminTeacherUpdateRequest getUpdateRequest() {
    return updateRequest;
  }

  public void setUpdateRequest(AdminTeacherUpdateRequest updateRequest) {
    this.updateRequest = updateRequest;
  }
}