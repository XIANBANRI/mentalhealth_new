package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.kafka.AdminProfileRequestProducer;
import com.sl.mentalhealth.kafka.message.AdminProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminProfileResponseMessage;
import com.sl.mentalhealth.vo.AdminProfileResponseVO;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AdminProfileGatewayServiceTest {

  @Mock
  private AdminProfileRequestProducer adminProfileRequestProducer;

  @Mock
  private PendingAdminProfileService pendingAdminProfileService;

  @InjectMocks
  private AdminProfileGatewayService service;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(service, "timeoutSeconds", 10L);
  }

  @Test
  void getAdminProfile_success() {
    AdminProfileResponseVO data = org.mockito.Mockito.mock(AdminProfileResponseVO.class);
    AdminProfileResponseMessage response = new AdminProfileResponseMessage();
    response.setSuccess(true);
    response.setData(data);

    CompletableFuture<AdminProfileResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAdminProfileService.create(anyString())).thenReturn(future);

    AdminProfileResponseVO result = service.getAdminProfile("  admin001  ");

    assertSame(data, result);

    ArgumentCaptor<AdminProfileRequestMessage> captor =
        ArgumentCaptor.forClass(AdminProfileRequestMessage.class);
    verify(adminProfileRequestProducer).send(captor.capture());
    AdminProfileRequestMessage sent = captor.getValue();

    assertEquals(AdminProfileRequestMessage.ACTION_QUERY_PROFILE, sent.getAction());
    assertEquals("admin001", sent.getAccount());

    verify(pendingAdminProfileService).create(sent.getRequestId());
    verify(pendingAdminProfileService).remove(sent.getRequestId());
  }

  @Test
  void getAdminProfile_blankAccount_throwsIllegalArgumentException() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> service.getAdminProfile("   "));

    assertEquals("管理员账号不能为空", ex.getMessage());
    verify(pendingAdminProfileService, never()).create(anyString());
    verify(adminProfileRequestProducer, never()).send(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void getAdminProfile_responseFail_wrapsRuntimeException() {
    AdminProfileResponseMessage response = new AdminProfileResponseMessage();
    response.setSuccess(false);
    response.setMessage("查询失败");

    CompletableFuture<AdminProfileResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAdminProfileService.create(anyString())).thenReturn(future);

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> service.getAdminProfile("admin001"));

    assertEquals("查询管理员信息失败：查询失败", ex.getMessage());
    verify(pendingAdminProfileService).remove(anyString());
  }

  @Test
  void updateAvatar_success() {
    AdminProfileResponseVO data = org.mockito.Mockito.mock(AdminProfileResponseVO.class);
    AdminProfileResponseMessage response = new AdminProfileResponseMessage();
    response.setSuccess(true);
    response.setData(data);

    CompletableFuture<AdminProfileResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAdminProfileService.create(anyString())).thenReturn(future);

    AdminProfileResponseVO result =
        service.updateAvatar(" admin001 ", " /avatar/admin/a.png ");

    assertSame(data, result);

    ArgumentCaptor<AdminProfileRequestMessage> captor =
        ArgumentCaptor.forClass(AdminProfileRequestMessage.class);
    verify(adminProfileRequestProducer).send(captor.capture());
    AdminProfileRequestMessage sent = captor.getValue();

    assertEquals(AdminProfileRequestMessage.ACTION_UPDATE_AVATAR, sent.getAction());
    assertEquals("admin001", sent.getAccount());
    assertEquals("/avatar/admin/a.png", sent.getAvatarUrl());

    verify(pendingAdminProfileService).remove(sent.getRequestId());
  }

  @Test
  void updateAvatar_blankAvatar_throwsIllegalArgumentException() {
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> service.updateAvatar("admin001", "   "));

    assertEquals("头像地址不能为空", ex.getMessage());
    verify(pendingAdminProfileService, never()).create(anyString());
  }
}