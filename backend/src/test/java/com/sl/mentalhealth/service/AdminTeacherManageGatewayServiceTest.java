package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.dto.AdminTeacherCreateRequest;
import com.sl.mentalhealth.dto.AdminTeacherQueryRequest;
import com.sl.mentalhealth.dto.AdminTeacherUpdateRequest;
import com.sl.mentalhealth.kafka.AdminTeacherManageRequestProducer;
import com.sl.mentalhealth.kafka.message.AdminTeacherManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminTeacherManageResponseMessage;
import com.sl.mentalhealth.vo.AdminTeacherPageVO;
import com.sl.mentalhealth.vo.AdminTeacherVO;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminTeacherManageGatewayServiceTest {

  @Mock
  private AdminTeacherManageRequestProducer requestProducer;

  @Mock
  private PendingAdminTeacherManageService pendingService;

  @InjectMocks
  private AdminTeacherManageGatewayService service;

  @Test
  void queryPage_success() {
    AdminTeacherQueryRequest request = org.mockito.Mockito.mock(AdminTeacherQueryRequest.class);
    CompletableFuture<AdminTeacherManageResponseMessage> future = new CompletableFuture<>();
    AdminTeacherManageResponseMessage response = new AdminTeacherManageResponseMessage();
    AdminTeacherPageVO page = org.mockito.Mockito.mock(AdminTeacherPageVO.class);
    response.setSuccess(true);
    response.setPage(page);

    when(pendingService.create(anyString())).thenReturn(future);
    when(pendingService.await(anyString(), eq(future))).thenReturn(response);

    AdminTeacherPageVO result = service.queryPage(request);

    assertSame(page, result);

    ArgumentCaptor<AdminTeacherManageRequestMessage> captor =
        ArgumentCaptor.forClass(AdminTeacherManageRequestMessage.class);
    verify(requestProducer).send(captor.capture());
    AdminTeacherManageRequestMessage sent = captor.getValue();

    assertEquals(AdminTeacherManageRequestMessage.ACTION_QUERY_PAGE, sent.getAction());
    assertSame(request, sent.getQueryRequest());
    assertEquals(32, sent.getRequestId().length());

    verify(pendingService).create(sent.getRequestId());
    verify(pendingService).await(sent.getRequestId(), future);
  }

  @Test
  void detail_success() {
    CompletableFuture<AdminTeacherManageResponseMessage> future = new CompletableFuture<>();
    AdminTeacherManageResponseMessage response = new AdminTeacherManageResponseMessage();
    AdminTeacherVO teacher = org.mockito.Mockito.mock(AdminTeacherVO.class);
    response.setSuccess(true);
    response.setTeacher(teacher);

    when(pendingService.create(anyString())).thenReturn(future);
    when(pendingService.await(anyString(), eq(future))).thenReturn(response);

    AdminTeacherVO result = service.detail("t001");

    assertSame(teacher, result);

    ArgumentCaptor<AdminTeacherManageRequestMessage> captor =
        ArgumentCaptor.forClass(AdminTeacherManageRequestMessage.class);
    verify(requestProducer).send(captor.capture());
    AdminTeacherManageRequestMessage sent = captor.getValue();

    assertEquals(AdminTeacherManageRequestMessage.ACTION_DETAIL, sent.getAction());
    assertEquals("t001", sent.getAccount());
  }

  @Test
  void create_success() {
    AdminTeacherCreateRequest request = org.mockito.Mockito.mock(AdminTeacherCreateRequest.class);
    CompletableFuture<AdminTeacherManageResponseMessage> future = new CompletableFuture<>();
    AdminTeacherManageResponseMessage response = new AdminTeacherManageResponseMessage();
    AdminTeacherVO teacher = org.mockito.Mockito.mock(AdminTeacherVO.class);
    response.setSuccess(true);
    response.setTeacher(teacher);

    when(pendingService.create(anyString())).thenReturn(future);
    when(pendingService.await(anyString(), eq(future))).thenReturn(response);

    AdminTeacherVO result = service.create(request);

    assertSame(teacher, result);

    ArgumentCaptor<AdminTeacherManageRequestMessage> captor =
        ArgumentCaptor.forClass(AdminTeacherManageRequestMessage.class);
    verify(requestProducer).send(captor.capture());
    AdminTeacherManageRequestMessage sent = captor.getValue();

    assertEquals(AdminTeacherManageRequestMessage.ACTION_CREATE, sent.getAction());
    assertSame(request, sent.getCreateRequest());
  }

  @Test
  void update_success() {
    AdminTeacherUpdateRequest request = org.mockito.Mockito.mock(AdminTeacherUpdateRequest.class);
    CompletableFuture<AdminTeacherManageResponseMessage> future = new CompletableFuture<>();
    AdminTeacherManageResponseMessage response = new AdminTeacherManageResponseMessage();
    AdminTeacherVO teacher = org.mockito.Mockito.mock(AdminTeacherVO.class);
    response.setSuccess(true);
    response.setTeacher(teacher);

    when(pendingService.create(anyString())).thenReturn(future);
    when(pendingService.await(anyString(), eq(future))).thenReturn(response);

    AdminTeacherVO result = service.update(request);

    assertSame(teacher, result);

    ArgumentCaptor<AdminTeacherManageRequestMessage> captor =
        ArgumentCaptor.forClass(AdminTeacherManageRequestMessage.class);
    verify(requestProducer).send(captor.capture());
    AdminTeacherManageRequestMessage sent = captor.getValue();

    assertEquals(AdminTeacherManageRequestMessage.ACTION_UPDATE, sent.getAction());
    assertSame(request, sent.getUpdateRequest());
  }

  @Test
  void detail_fail_throwsException() {
    CompletableFuture<AdminTeacherManageResponseMessage> future = new CompletableFuture<>();
    AdminTeacherManageResponseMessage response = new AdminTeacherManageResponseMessage();
    response.setSuccess(false);
    response.setMessage("教师不存在");

    when(pendingService.create(anyString())).thenReturn(future);
    when(pendingService.await(anyString(), eq(future))).thenReturn(response);

    RuntimeException ex = assertThrows(RuntimeException.class, () -> service.detail("t001"));

    assertEquals("教师不存在", ex.getMessage());
  }
}