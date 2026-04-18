package com.sl.mentalhealth.kafka.message;

import com.sl.mentalhealth.dto.AdminCounselorClassesUpdateRequest;
import com.sl.mentalhealth.dto.AdminCounselorCreateRequest;
import com.sl.mentalhealth.dto.AdminCounselorQueryRequest;
import com.sl.mentalhealth.dto.AdminCounselorUpdateRequest;

public class AdminCounselorManageRequestMessage {

  public static final String ACTION_QUERY_PAGE = "QUERY_PAGE";
  public static final String ACTION_DETAIL = "DETAIL";
  public static final String ACTION_CREATE = "CREATE";
  public static final String ACTION_UPDATE = "UPDATE";
  public static final String ACTION_UPDATE_CLASSES = "UPDATE_CLASSES";

  private String requestId;
  private String action;
  private String account;
  private AdminCounselorQueryRequest queryRequest;
  private AdminCounselorCreateRequest createRequest;
  private AdminCounselorUpdateRequest updateRequest;
  private AdminCounselorClassesUpdateRequest classesUpdateRequest;

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

  public AdminCounselorQueryRequest getQueryRequest() {
    return queryRequest;
  }

  public void setQueryRequest(AdminCounselorQueryRequest queryRequest) {
    this.queryRequest = queryRequest;
  }

  public AdminCounselorCreateRequest getCreateRequest() {
    return createRequest;
  }

  public void setCreateRequest(AdminCounselorCreateRequest createRequest) {
    this.createRequest = createRequest;
  }

  public AdminCounselorUpdateRequest getUpdateRequest() {
    return updateRequest;
  }

  public void setUpdateRequest(AdminCounselorUpdateRequest updateRequest) {
    this.updateRequest = updateRequest;
  }

  public AdminCounselorClassesUpdateRequest getClassesUpdateRequest() {
    return classesUpdateRequest;
  }

  public void setClassesUpdateRequest(
      AdminCounselorClassesUpdateRequest classesUpdateRequest) {
    this.classesUpdateRequest = classesUpdateRequest;
  }
}