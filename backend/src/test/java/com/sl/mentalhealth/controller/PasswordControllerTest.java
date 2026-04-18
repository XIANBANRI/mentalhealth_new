package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.dto.ResetPasswordRequest;
import com.sl.mentalhealth.service.PasswordGatewayService;
import com.sl.mentalhealth.vo.ResetPasswordResponseVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordControllerTest {

  @Mock
  private PasswordGatewayService passwordGatewayService;

  @InjectMocks
  private PasswordController controller;

  @Test
  void resetPassword_success() {
    ResetPasswordRequest request = mock(ResetPasswordRequest.class);
    ResetPasswordResponseVO responseVO = mock(ResetPasswordResponseVO.class);

    when(responseVO.getMessage()).thenReturn("密码重置成功");
    when(passwordGatewayService.resetPassword(request)).thenReturn(responseVO);

    ResponseEntity<Map<String, Object>> response = controller.resetPassword(request);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(true, response.getBody().get("success"));
    assertEquals("密码重置成功", response.getBody().get("message"));

    verify(passwordGatewayService).resetPassword(request);
  }

  @Test
  void resetPassword_whenExceptionWithMessage_shouldReturnBadRequest() {
    ResetPasswordRequest request = mock(ResetPasswordRequest.class);

    when(passwordGatewayService.resetPassword(request))
        .thenThrow(new RuntimeException("原密码错误"));

    ResponseEntity<Map<String, Object>> response = controller.resetPassword(request);

    assertEquals(400, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(false, response.getBody().get("success"));
    assertEquals("原密码错误", response.getBody().get("message"));

    verify(passwordGatewayService).resetPassword(request);
  }

  @Test
  void resetPassword_whenExceptionWithoutMessage_shouldReturnDefaultMessage() {
    ResetPasswordRequest request = mock(ResetPasswordRequest.class);

    when(passwordGatewayService.resetPassword(request))
        .thenThrow(new RuntimeException());

    ResponseEntity<Map<String, Object>> response = controller.resetPassword(request);

    assertEquals(400, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(false, response.getBody().get("success"));
    assertEquals("密码重置失败", response.getBody().get("message"));
  }
}