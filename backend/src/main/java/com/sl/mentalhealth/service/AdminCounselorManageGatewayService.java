package com.sl.mentalhealth.service;

import com.sl.mentalhealth.dto.AdminCounselorClassesUpdateRequest;
import com.sl.mentalhealth.dto.AdminCounselorCreateRequest;
import com.sl.mentalhealth.dto.AdminCounselorQueryRequest;
import com.sl.mentalhealth.dto.AdminCounselorUpdateRequest;
import com.sl.mentalhealth.kafka.AdminCounselorManageRequestProducer;
import com.sl.mentalhealth.kafka.message.AdminCounselorManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminCounselorManageResponseMessage;
import com.sl.mentalhealth.vo.AdminCounselorDetailVO;
import com.sl.mentalhealth.vo.AdminCounselorPageVO;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

@Service
public class AdminCounselorManageGatewayService {

  private final AdminCounselorManageRequestProducer requestProducer;
  private final PendingAdminCounselorManageService pendingService;

  public AdminCounselorManageGatewayService(
      AdminCounselorManageRequestProducer requestProducer,
      PendingAdminCounselorManageService pendingService) {
    this.requestProducer = requestProducer;
    this.pendingService = pendingService;
  }

  public AdminCounselorPageVO queryPage(AdminCounselorQueryRequest request) {
    AdminCounselorManageRequestMessage message = new AdminCounselorManageRequestMessage();
    message.setAction(AdminCounselorManageRequestMessage.ACTION_QUERY_PAGE);
    message.setQueryRequest(request);
    AdminCounselorManageResponseMessage response = dispatch(message);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getPage();
  }

  public AdminCounselorDetailVO detail(String account) {
    AdminCounselorManageRequestMessage message = new AdminCounselorManageRequestMessage();
    message.setAction(AdminCounselorManageRequestMessage.ACTION_DETAIL);
    message.setAccount(account);
    AdminCounselorManageResponseMessage response = dispatch(message);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getDetail();
  }

  public AdminCounselorDetailVO create(AdminCounselorCreateRequest request) {
    AdminCounselorManageRequestMessage message = new AdminCounselorManageRequestMessage();
    message.setAction(AdminCounselorManageRequestMessage.ACTION_CREATE);
    message.setCreateRequest(request);
    AdminCounselorManageResponseMessage response = dispatch(message);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getDetail();
  }

  public AdminCounselorDetailVO update(AdminCounselorUpdateRequest request) {
    AdminCounselorManageRequestMessage message = new AdminCounselorManageRequestMessage();
    message.setAction(AdminCounselorManageRequestMessage.ACTION_UPDATE);
    message.setUpdateRequest(request);
    AdminCounselorManageResponseMessage response = dispatch(message);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getDetail();
  }

  public AdminCounselorDetailVO updateClasses(AdminCounselorClassesUpdateRequest request) {
    AdminCounselorManageRequestMessage message = new AdminCounselorManageRequestMessage();
    message.setAction(AdminCounselorManageRequestMessage.ACTION_UPDATE_CLASSES);
    message.setClassesUpdateRequest(request);
    AdminCounselorManageResponseMessage response = dispatch(message);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getDetail();
  }

  private AdminCounselorManageResponseMessage dispatch(
      AdminCounselorManageRequestMessage message) {
    String requestId = UUID.randomUUID().toString().replace("-", "");
    message.setRequestId(requestId);
    CompletableFuture<AdminCounselorManageResponseMessage> future =
        pendingService.create(requestId);
    requestProducer.send(message);
    return pendingService.await(requestId, future);
  }
}