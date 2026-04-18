package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.LoginRequestMessage;
import com.sl.mentalhealth.kafka.message.LoginResponseMessage;
import com.sl.mentalhealth.service.LocalAuthService;
import com.sl.mentalhealth.vo.LoginResponseVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginRequestConsumerTest {

  @Mock
  private LocalAuthService localAuthService;

  @Mock
  private LoginResponseProducer loginResponseProducer;

  @InjectMocks
  private LoginRequestConsumer consumer;

  @Test
  void onMessage_success() {
    LoginRequestMessage request = mock(LoginRequestMessage.class);
    LoginResponseVO vo = mock(LoginResponseVO.class);

    when(request.getRequestId()).thenReturn("req-001");
    when(request.getRole()).thenReturn("student");
    when(request.getUsername()).thenReturn("zhangsan");
    when(request.getPassword()).thenReturn("123456");

    when(vo.getRole()).thenReturn("student");
    when(vo.getUsername()).thenReturn("zhangsan");
    when(vo.getRedirectPath()).thenReturn("/student/home");

    when(localAuthService.login("student", "zhangsan", "123456")).thenReturn(vo);

    consumer.onMessage(request);

    verify(localAuthService, times(1)).login("student", "zhangsan", "123456");

    LoginResponseMessage response = captureResponse();
    assertEquals("req-001", response.getRequestId());
    assertTrue(response.getSuccess());
    assertEquals("登录成功", response.getMessage());
    assertEquals("student", response.getRole());
    assertEquals("zhangsan", response.getUsername());
    assertEquals("/student/home", response.getRedirectPath());
  }

  @Test
  void onMessage_fail_useExceptionMessage() {
    LoginRequestMessage request = mock(LoginRequestMessage.class);

    when(request.getRequestId()).thenReturn("req-002");
    when(request.getRole()).thenReturn("student");
    when(request.getUsername()).thenReturn("zhangsan");
    when(request.getPassword()).thenReturn("bad-pwd");

    when(localAuthService.login("student", "zhangsan", "bad-pwd"))
        .thenThrow(new RuntimeException("用户名或密码错误"));

    consumer.onMessage(request);

    verify(localAuthService, times(1)).login("student", "zhangsan", "bad-pwd");

    LoginResponseMessage response = captureResponse();
    assertEquals("req-002", response.getRequestId());
    assertFalse(response.getSuccess());
    assertEquals("用户名或密码错误", response.getMessage());
    assertEquals("student", response.getRole());
    assertEquals("zhangsan", response.getUsername());
    assertNull(response.getRedirectPath());
  }

  @Test
  void onMessage_fail_useDefaultMessageWhenExceptionMessageIsNull() {
    LoginRequestMessage request = mock(LoginRequestMessage.class);

    when(request.getRequestId()).thenReturn("req-003");
    when(request.getRole()).thenReturn("admin");
    when(request.getUsername()).thenReturn("admin1");
    when(request.getPassword()).thenReturn("bad");

    when(localAuthService.login("admin", "admin1", "bad"))
        .thenThrow(new RuntimeException((String) null));

    consumer.onMessage(request);

    LoginResponseMessage response = captureResponse();
    assertEquals("req-003", response.getRequestId());
    assertFalse(response.getSuccess());
    assertEquals("登录失败", response.getMessage());
    assertEquals("admin", response.getRole());
    assertEquals("admin1", response.getUsername());
    assertNull(response.getRedirectPath());
  }

  private LoginResponseMessage captureResponse() {
    ArgumentCaptor<LoginResponseMessage> captor =
        ArgumentCaptor.forClass(LoginResponseMessage.class);
    verify(loginResponseProducer, atLeastOnce()).send(captor.capture());
    return captor.getValue();
  }
}