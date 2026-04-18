package com.sl.mentalhealth.service;

import com.sl.mentalhealth.dto.AdminTeacherCreateRequest;
import com.sl.mentalhealth.dto.AdminTeacherQueryRequest;
import com.sl.mentalhealth.dto.AdminTeacherUpdateRequest;
import com.sl.mentalhealth.kafka.AdminTeacherManageRequestProducer;
import com.sl.mentalhealth.kafka.message.AdminTeacherManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminTeacherManageResponseMessage;
import com.sl.mentalhealth.vo.AdminTeacherPageVO;
import com.sl.mentalhealth.vo.AdminTeacherVO;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

@Service
public class AdminTeacherManageGatewayService {

  private final AdminTeacherManageRequestProducer requestProducer;
  private final PendingAdminTeacherManageService pendingService;

  public AdminTeacherManageGatewayService(
      AdminTeacherManageRequestProducer requestProducer,
      PendingAdminTeacherManageService pendingService) {
    this.requestProducer = requestProducer;
    this.pendingService = pendingService;
  }

  public AdminTeacherPageVO queryPage(AdminTeacherQueryRequest request) {
    AdminTeacherManageRequestMessage message = new AdminTeacherManageRequestMessage();
    message.setAction(AdminTeacherManageRequestMessage.ACTION_QUERY_PAGE);
    message.setQueryRequest(request);
    AdminTeacherManageResponseMessage response = dispatch(message);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getPage();
  }

  public AdminTeacherVO detail(String account) {
    AdminTeacherManageRequestMessage message = new AdminTeacherManageRequestMessage();
    message.setAction(AdminTeacherManageRequestMessage.ACTION_DETAIL);
    message.setAccount(account);
    AdminTeacherManageResponseMessage response = dispatch(message);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getTeacher();
  }

  public AdminTeacherVO create(AdminTeacherCreateRequest request) {
    AdminTeacherManageRequestMessage message = new AdminTeacherManageRequestMessage();
    message.setAction(AdminTeacherManageRequestMessage.ACTION_CREATE);
    message.setCreateRequest(request);
    AdminTeacherManageResponseMessage response = dispatch(message);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getTeacher();
  }

  public AdminTeacherVO update(AdminTeacherUpdateRequest request) {
    AdminTeacherManageRequestMessage message = new AdminTeacherManageRequestMessage();
    message.setAction(AdminTeacherManageRequestMessage.ACTION_UPDATE);
    message.setUpdateRequest(request);
    AdminTeacherManageResponseMessage response = dispatch(message);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getTeacher();
  }

  private AdminTeacherManageResponseMessage dispatch(AdminTeacherManageRequestMessage message) {
    String requestId = UUID.randomUUID().toString().replace("-", "");
    message.setRequestId(requestId);
    CompletableFuture<AdminTeacherManageResponseMessage> future = pendingService.create(requestId);
    requestProducer.send(message);
    return pendingService.await(requestId, future);
  }
}