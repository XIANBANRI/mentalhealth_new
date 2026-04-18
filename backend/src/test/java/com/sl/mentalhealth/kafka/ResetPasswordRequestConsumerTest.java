package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.ResetPasswordRequestMessage;
import com.sl.mentalhealth.kafka.message.ResetPasswordResponseMessage;
import com.sl.mentalhealth.service.LocalPasswordService;
import com.sl.mentalhealth.vo.ResetPasswordResponseVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordRequestConsumerTest {

  @Mock
  private LocalPasswordService localPasswordService;

  @Mock
  private ResetPasswordResponseProducer resetPasswordResponseProducer;

  @InjectMocks
  private ResetPasswordRequestConsumer consumer;

  @Test
  void onMessage_success() {
    ResetPasswordRequestMessage request = mock(ResetPasswordRequestMessage.class);
    ResetPasswordResponseVO vo = mock(ResetPasswordResponseVO.class);

    when(request.getRequestId()).thenReturn("req-001");
    when(request.getRole()).thenReturn("student");
    when(request.getUsername()).thenReturn("zhangsan");
    when(request.getPhone()).thenReturn("13800000000");
    when(request.getNewPassword()).thenReturn("new123456");

    when(vo.getMessage()).thenReturn("密码重置成功");

    when(localPasswordService.resetPassword("student", "zhangsan", "13800000000", "new123456"))
        .thenReturn(vo);

    consumer.onMessage(request);

    verify(localPasswordService, times(1))
        .resetPassword("student", "zhangsan", "13800000000", "new123456");

    ResetPasswordResponseMessage response = captureResponse();
    assertEquals("req-001", response.getRequestId());
    assertTrue(response.getSuccess());
    assertEquals("密码重置成功", response.getMessage());
  }

  @Test
  void onMessage_fail_useExceptionMessage() {
    ResetPasswordRequestMessage request = mock(ResetPasswordRequestMessage.class);

    when(request.getRequestId()).thenReturn("req-002");
    when(request.getRole()).thenReturn("teacher");
    when(request.getUsername()).thenReturn("lisi");
    when(request.getPhone()).thenReturn("13900000000");
    when(request.getNewPassword()).thenReturn("new123456");

    when(localPasswordService.resetPassword("teacher", "lisi", "13900000000", "new123456"))
        .thenThrow(new RuntimeException("手机号不匹配"));

    consumer.onMessage(request);

    verify(localPasswordService, times(1))
        .resetPassword("teacher", "lisi", "13900000000", "new123456");

    ResetPasswordResponseMessage response = captureResponse();
    assertEquals("req-002", response.getRequestId());
    assertFalse(response.getSuccess());
    assertEquals("手机号不匹配", response.getMessage());
  }

  @Test
  void onMessage_fail_useDefaultMessageWhenExceptionMessageIsNull() {
    ResetPasswordRequestMessage request = mock(ResetPasswordRequestMessage.class);

    when(request.getRequestId()).thenReturn("req-003");
    when(request.getRole()).thenReturn("admin");
    when(request.getUsername()).thenReturn("admin1");
    when(request.getPhone()).thenReturn("13700000000");
    when(request.getNewPassword()).thenReturn("new123456");

    when(localPasswordService.resetPassword("admin", "admin1", "13700000000", "new123456"))
        .thenThrow(new RuntimeException((String) null));

    consumer.onMessage(request);

    ResetPasswordResponseMessage response = captureResponse();
    assertEquals("req-003", response.getRequestId());
    assertFalse(response.getSuccess());
    assertEquals("密码重置失败", response.getMessage());
  }

  private ResetPasswordResponseMessage captureResponse() {
    ArgumentCaptor<ResetPasswordResponseMessage> captor =
        ArgumentCaptor.forClass(ResetPasswordResponseMessage.class);
    verify(resetPasswordResponseProducer, atLeastOnce()).send(captor.capture());
    return captor.getValue();
  }
}