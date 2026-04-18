package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.dto.LoginRequest;
import com.sl.mentalhealth.kafka.LoginRequestProducer;
import com.sl.mentalhealth.kafka.message.LoginRequestMessage;
import com.sl.mentalhealth.kafka.message.LoginResponseMessage;
import com.sl.mentalhealth.vo.LoginResponseVO;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthGatewayServiceTest {

  @Mock
  private LoginRequestProducer loginRequestProducer;

  @Mock
  private PendingLoginService pendingLoginService;

  @InjectMocks
  private AuthGatewayService service;

  @Test
  void login_success() {
    LoginRequest request = org.mockito.Mockito.mock(LoginRequest.class);
    when(request.getRole()).thenReturn("admin");
    when(request.getUsername()).thenReturn("admin001");
    when(request.getPassword()).thenReturn("123456");

    LoginResponseMessage response = new LoginResponseMessage();
    response.setSuccess(true);
    response.setRole("admin");
    response.setUsername("admin001");
    response.setRedirectPath("/admin/home");

    CompletableFuture<LoginResponseMessage> future = CompletableFuture.completedFuture(response);
    when(pendingLoginService.put(anyString())).thenReturn(future);

    LoginResponseVO result = service.login(request);

    assertEquals("admin", result.getRole());
    assertEquals("admin001", result.getUsername());
    assertEquals("/admin/home", result.getRedirectPath());

    ArgumentCaptor<LoginRequestMessage> captor = ArgumentCaptor.forClass(LoginRequestMessage.class);
    verify(loginRequestProducer).send(captor.capture());
    LoginRequestMessage sent = captor.getValue();
    assertEquals("admin", sent.getRole());
    assertEquals("admin001", sent.getUsername());
    assertEquals("123456", sent.getPassword());
  }

  @Test
  void login_blankField_throwsIllegalArgumentException() {
    LoginRequest request = org.mockito.Mockito.mock(LoginRequest.class);
    when(request.getRole()).thenReturn("admin");
    when(request.getUsername()).thenReturn(" ");

    IllegalArgumentException ex = assertThrows(
        IllegalArgumentException.class,
        () -> service.login(request)
    );

    assertEquals("请填写完整信息", ex.getMessage());
    verify(pendingLoginService, never()).put(anyString());
    verify(loginRequestProducer, never()).send(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void login_responseFail_throwsRuntimeExceptionAndRemovesPending() {
    LoginRequest request = org.mockito.Mockito.mock(LoginRequest.class);
    when(request.getRole()).thenReturn("admin");
    when(request.getUsername()).thenReturn("admin001");
    when(request.getPassword()).thenReturn("123456");

    LoginResponseMessage response = new LoginResponseMessage();
    response.setSuccess(false);
    response.setMessage("账号或密码错误");

    CompletableFuture<LoginResponseMessage> future = CompletableFuture.completedFuture(response);
    when(pendingLoginService.put(anyString())).thenReturn(future);

    RuntimeException ex = assertThrows(RuntimeException.class, () -> service.login(request));

    assertEquals("账号或密码错误", ex.getMessage());
    verify(pendingLoginService).remove(anyString());
  }

  @Test
  void login_futureException_prefersCauseMessage() {
    LoginRequest request = org.mockito.Mockito.mock(LoginRequest.class);
    when(request.getRole()).thenReturn("admin");
    when(request.getUsername()).thenReturn("admin001");
    when(request.getPassword()).thenReturn("123456");

    CompletableFuture<LoginResponseMessage> future = new CompletableFuture<>();
    future.completeExceptionally(new RuntimeException("底层错误"));
    when(pendingLoginService.put(anyString())).thenReturn(future);

    RuntimeException ex = assertThrows(RuntimeException.class, () -> service.login(request));

    assertEquals("底层错误", ex.getMessage());
    verify(pendingLoginService).remove(anyString());
  }
}