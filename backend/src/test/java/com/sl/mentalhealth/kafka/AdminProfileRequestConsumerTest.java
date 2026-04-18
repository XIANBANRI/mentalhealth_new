package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminProfileResponseMessage;
import com.sl.mentalhealth.service.LocalAdminProfileService;
import com.sl.mentalhealth.vo.AdminProfileResponseVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminProfileRequestConsumerTest {

  @Mock
  private LocalAdminProfileService localAdminProfileService;

  @Mock
  private AdminProfileResponseProducer adminProfileResponseProducer;

  @InjectMocks
  private AdminProfileRequestConsumer consumer;

  @Test
  void consume_nullMessage_doNothing() {
    consumer.consume(null);

    verifyNoInteractions(localAdminProfileService, adminProfileResponseProducer);
  }

  @Test
  void consume_updateAvatar_success() {
    AdminProfileRequestMessage message = mock(AdminProfileRequestMessage.class);
    AdminProfileResponseVO data = mock(AdminProfileResponseVO.class);

    when(message.getAction()).thenReturn(AdminProfileRequestMessage.ACTION_UPDATE_AVATAR);
    when(message.getAccount()).thenReturn("admin001");
    when(message.getAvatarUrl()).thenReturn("/avatar/admin/test.png");
    when(message.getRequestId()).thenReturn("req-001");

    when(localAdminProfileService.updateAvatar("admin001", "/avatar/admin/test.png"))
        .thenReturn(data);

    consumer.consume(message);

    verify(localAdminProfileService, times(1))
        .updateAvatar("admin001", "/avatar/admin/test.png");
    verify(localAdminProfileService, never()).getAdminProfile(anyString());

    ArgumentCaptor<AdminProfileResponseMessage> captor =
        ArgumentCaptor.forClass(AdminProfileResponseMessage.class);
    verify(adminProfileResponseProducer, times(1)).send(captor.capture());

    AdminProfileResponseMessage sent = captor.getValue();
    assertEquals("req-001", sent.getRequestId());
    assertEquals("头像上传成功", sent.getMessage());
    assertSame(data, sent.getData());
  }

  @Test
  void consume_getProfile_success() {
    AdminProfileRequestMessage message = mock(AdminProfileRequestMessage.class);
    AdminProfileResponseVO data = mock(AdminProfileResponseVO.class);

    when(message.getAction()).thenReturn("GET_PROFILE");
    when(message.getAccount()).thenReturn("admin001");
    when(message.getRequestId()).thenReturn("req-002");

    when(localAdminProfileService.getAdminProfile("admin001")).thenReturn(data);

    consumer.consume(message);

    verify(localAdminProfileService, times(1)).getAdminProfile("admin001");
    verify(localAdminProfileService, never()).updateAvatar(anyString(), anyString());

    ArgumentCaptor<AdminProfileResponseMessage> captor =
        ArgumentCaptor.forClass(AdminProfileResponseMessage.class);
    verify(adminProfileResponseProducer, times(1)).send(captor.capture());

    AdminProfileResponseMessage sent = captor.getValue();
    assertEquals("req-002", sent.getRequestId());
    assertEquals("查询管理员信息成功", sent.getMessage());
    assertSame(data, sent.getData());
  }

  @Test
  void consume_exception_sendFailResponse() {
    AdminProfileRequestMessage message = mock(AdminProfileRequestMessage.class);

    when(message.getAction()).thenReturn(AdminProfileRequestMessage.ACTION_UPDATE_AVATAR);
    when(message.getAccount()).thenReturn("admin001");
    when(message.getAvatarUrl()).thenReturn("/avatar/admin/test.png");
    when(message.getRequestId()).thenReturn("req-003");

    when(localAdminProfileService.updateAvatar("admin001", "/avatar/admin/test.png"))
        .thenThrow(new RuntimeException("上传失败"));

    consumer.consume(message);

    ArgumentCaptor<AdminProfileResponseMessage> captor =
        ArgumentCaptor.forClass(AdminProfileResponseMessage.class);
    verify(adminProfileResponseProducer, times(1)).send(captor.capture());

    AdminProfileResponseMessage sent = captor.getValue();
    assertEquals("req-003", sent.getRequestId());
    assertEquals("上传失败", sent.getMessage());
    assertNull(sent.getData());
  }
}