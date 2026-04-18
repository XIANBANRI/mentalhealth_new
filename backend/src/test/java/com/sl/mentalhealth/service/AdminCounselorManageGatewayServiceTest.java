package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.dto.AdminCounselorClassesUpdateRequest;
import com.sl.mentalhealth.dto.AdminCounselorCreateRequest;
import com.sl.mentalhealth.dto.AdminCounselorQueryRequest;
import com.sl.mentalhealth.dto.AdminCounselorUpdateRequest;
import com.sl.mentalhealth.kafka.AdminCounselorManageRequestProducer;
import com.sl.mentalhealth.kafka.message.AdminCounselorManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminCounselorManageResponseMessage;
import com.sl.mentalhealth.vo.AdminCounselorDetailVO;
import com.sl.mentalhealth.vo.AdminCounselorPageVO;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminCounselorManageGatewayServiceTest {

  @Mock
  private AdminCounselorManageRequestProducer requestProducer;

  @Mock
  private PendingAdminCounselorManageService pendingService;

  @InjectMocks
  private AdminCounselorManageGatewayService service;

  @Test
  void queryPage_success() {
    AdminCounselorQueryRequest request = org.mockito.Mockito.mock(AdminCounselorQueryRequest.class);
    CompletableFuture<AdminCounselorManageResponseMessage> future = new CompletableFuture<>();
    AdminCounselorManageResponseMessage response = new AdminCounselorManageResponseMessage();
    AdminCounselorPageVO page = org.mockito.Mockito.mock(AdminCounselorPageVO.class);
    response.setSuccess(true);
    response.setPage(page);

    when(pendingService.create(anyString())).thenReturn(future);
    when(pendingService.await(anyString(), eq(future))).thenReturn(response);

    AdminCounselorPageVO result = service.queryPage(request);

    assertSame(page, result);

    ArgumentCaptor<AdminCounselorManageRequestMessage> captor =
        ArgumentCaptor.forClass(AdminCounselorManageRequestMessage.class);
    verify(requestProducer).send(captor.capture());
    AdminCounselorManageRequestMessage sent = captor.getValue();

    assertEquals(AdminCounselorManageRequestMessage.ACTION_QUERY_PAGE, sent.getAction());
    assertSame(request, sent.getQueryRequest());
    assertEquals(32, sent.getRequestId().length());

    verify(pendingService).create(sent.getRequestId());
    verify(pendingService).await(sent.getRequestId(), future);
  }

  @Test
  void detail_success() {
    CompletableFuture<AdminCounselorManageResponseMessage> future = new CompletableFuture<>();
    AdminCounselorManageResponseMessage response = new AdminCounselorManageResponseMessage();
    AdminCounselorDetailVO detail = org.mockito.Mockito.mock(AdminCounselorDetailVO.class);
    response.setSuccess(true);
    response.setDetail(detail);

    when(pendingService.create(anyString())).thenReturn(future);
    when(pendingService.await(anyString(), eq(future))).thenReturn(response);

    AdminCounselorDetailVO result = service.detail("c001");

    assertSame(detail, result);

    ArgumentCaptor<AdminCounselorManageRequestMessage> captor =
        ArgumentCaptor.forClass(AdminCounselorManageRequestMessage.class);
    verify(requestProducer).send(captor.capture());
    AdminCounselorManageRequestMessage sent = captor.getValue();

    assertEquals(AdminCounselorManageRequestMessage.ACTION_DETAIL, sent.getAction());
    assertEquals("c001", sent.getAccount());
  }

  @Test
  void create_success() {
    AdminCounselorCreateRequest request = org.mockito.Mockito.mock(AdminCounselorCreateRequest.class);
    CompletableFuture<AdminCounselorManageResponseMessage> future = new CompletableFuture<>();
    AdminCounselorManageResponseMessage response = new AdminCounselorManageResponseMessage();
    AdminCounselorDetailVO detail = org.mockito.Mockito.mock(AdminCounselorDetailVO.class);
    response.setSuccess(true);
    response.setDetail(detail);

    when(pendingService.create(anyString())).thenReturn(future);
    when(pendingService.await(anyString(), eq(future))).thenReturn(response);

    AdminCounselorDetailVO result = service.create(request);

    assertSame(detail, result);

    ArgumentCaptor<AdminCounselorManageRequestMessage> captor =
        ArgumentCaptor.forClass(AdminCounselorManageRequestMessage.class);
    verify(requestProducer).send(captor.capture());
    AdminCounselorManageRequestMessage sent = captor.getValue();

    assertEquals(AdminCounselorManageRequestMessage.ACTION_CREATE, sent.getAction());
    assertSame(request, sent.getCreateRequest());
  }

  @Test
  void update_success() {
    AdminCounselorUpdateRequest request = org.mockito.Mockito.mock(AdminCounselorUpdateRequest.class);
    CompletableFuture<AdminCounselorManageResponseMessage> future = new CompletableFuture<>();
    AdminCounselorManageResponseMessage response = new AdminCounselorManageResponseMessage();
    AdminCounselorDetailVO detail = org.mockito.Mockito.mock(AdminCounselorDetailVO.class);
    response.setSuccess(true);
    response.setDetail(detail);

    when(pendingService.create(anyString())).thenReturn(future);
    when(pendingService.await(anyString(), eq(future))).thenReturn(response);

    AdminCounselorDetailVO result = service.update(request);

    assertSame(detail, result);

    ArgumentCaptor<AdminCounselorManageRequestMessage> captor =
        ArgumentCaptor.forClass(AdminCounselorManageRequestMessage.class);
    verify(requestProducer).send(captor.capture());
    AdminCounselorManageRequestMessage sent = captor.getValue();

    assertEquals(AdminCounselorManageRequestMessage.ACTION_UPDATE, sent.getAction());
    assertSame(request, sent.getUpdateRequest());
  }

  @Test
  void updateClasses_success() {
    AdminCounselorClassesUpdateRequest request =
        org.mockito.Mockito.mock(AdminCounselorClassesUpdateRequest.class);
    CompletableFuture<AdminCounselorManageResponseMessage> future = new CompletableFuture<>();
    AdminCounselorManageResponseMessage response = new AdminCounselorManageResponseMessage();
    AdminCounselorDetailVO detail = org.mockito.Mockito.mock(AdminCounselorDetailVO.class);
    response.setSuccess(true);
    response.setDetail(detail);

    when(pendingService.create(anyString())).thenReturn(future);
    when(pendingService.await(anyString(), eq(future))).thenReturn(response);

    AdminCounselorDetailVO result = service.updateClasses(request);

    assertSame(detail, result);

    ArgumentCaptor<AdminCounselorManageRequestMessage> captor =
        ArgumentCaptor.forClass(AdminCounselorManageRequestMessage.class);
    verify(requestProducer).send(captor.capture());
    AdminCounselorManageRequestMessage sent = captor.getValue();

    assertEquals(AdminCounselorManageRequestMessage.ACTION_UPDATE_CLASSES, sent.getAction());
    assertSame(request, sent.getClassesUpdateRequest());
  }

  @Test
  void queryPage_fail_throwsException() {
    CompletableFuture<AdminCounselorManageResponseMessage> future = new CompletableFuture<>();
    AdminCounselorManageResponseMessage response = new AdminCounselorManageResponseMessage();
    response.setSuccess(false);
    response.setMessage("查询失败");

    when(pendingService.create(anyString())).thenReturn(future);
    when(pendingService.await(anyString(), eq(future))).thenReturn(response);

    RuntimeException ex =
        assertThrows(
            RuntimeException.class,
            () -> service.queryPage(org.mockito.Mockito.mock(AdminCounselorQueryRequest.class)));

    assertEquals("查询失败", ex.getMessage());
    verify(requestProducer).send(any(AdminCounselorManageRequestMessage.class));
  }
}