package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.LoginRequest;
import com.sl.mentalhealth.service.AuthGatewayService;
import com.sl.mentalhealth.vo.LoginResponseVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock
  private AuthGatewayService authGatewayService;

  @InjectMocks
  private AuthController controller;

  @Test
  void login_success() {
    LoginRequest request = mock(LoginRequest.class);
    LoginResponseVO responseVO = mock(LoginResponseVO.class);

    when(request.getRole()).thenReturn("admin");
    when(request.getUsername()).thenReturn("zhangsan");
    when(authGatewayService.login(request)).thenReturn(responseVO);

    Result<LoginResponseVO> result = controller.login(request);

    assertNotNull(result);
    assertEquals(200, result.getCode());
    assertEquals("登录成功", result.getMessage());
    assertSame(responseVO, result.getData());

    verify(authGatewayService).login(request);
  }

  @Test
  void login_whenIllegalArgumentException_shouldReturnBadRequest() {
    LoginRequest request = mock(LoginRequest.class);

    when(request.getRole()).thenReturn("teacher");
    when(request.getUsername()).thenReturn("lisi");
    when(authGatewayService.login(request))
        .thenThrow(new IllegalArgumentException("参数错误"));

    Result<LoginResponseVO> result = controller.login(request);

    assertNotNull(result);
    assertEquals(400, result.getCode());
    assertEquals("参数错误", result.getMessage());
    assertNull(result.getData());

    verify(authGatewayService).login(request);
  }

  @Test
  void login_whenException_shouldReturnError() {
    LoginRequest request = mock(LoginRequest.class);

    when(request.getRole()).thenReturn("counselor");
    when(request.getUsername()).thenReturn("wangwu");
    when(authGatewayService.login(request))
        .thenThrow(new RuntimeException("登录失败"));

    Result<LoginResponseVO> result = controller.login(request);

    assertNotNull(result);
    assertEquals(500, result.getCode());
    assertEquals("登录失败", result.getMessage());
    assertNull(result.getData());

    verify(authGatewayService).login(request);
  }
}