package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.config.LoginUser;
import com.sl.mentalhealth.config.UserContext;
import com.sl.mentalhealth.dto.AssessmentSubmitRequest;
import com.sl.mentalhealth.service.AssessmentGatewayService;
import org.junit.jupiter.api.AfterEach;
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
class StudentAssessmentControllerTest {

  @Mock
  private AssessmentGatewayService assessmentGatewayService;

  @InjectMocks
  private StudentAssessmentController controller;

  @AfterEach
  void tearDown() {
    UserContext.clear();
  }

  @Test
  void listScales_success() {
    ResponseEntity<Map<String, Object>> response = controller.listScales();

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(true, response.getBody().get("success"));
    assertEquals("查询成功", response.getBody().get("message"));
    assertTrue(response.getBody().containsKey("data"));

    verify(assessmentGatewayService).listScales();
  }

  @Test
  void listScales_whenException_shouldReturnBadRequest() {
    when(assessmentGatewayService.listScales()).thenThrow(new RuntimeException("查询失败"));

    ResponseEntity<Map<String, Object>> response = controller.listScales();

    assertEquals(400, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(false, response.getBody().get("success"));
    assertEquals("查询失败", response.getBody().get("message"));

    verify(assessmentGatewayService).listScales();
  }

  @Test
  void getDetail_success() {
    ResponseEntity<Map<String, Object>> response = controller.getDetail(1L);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(true, response.getBody().get("success"));
    assertEquals("查询成功", response.getBody().get("message"));
    assertTrue(response.getBody().containsKey("data"));

    verify(assessmentGatewayService).getDetail(1L);
  }

  @Test
  void getDetail_whenException_shouldReturnBadRequest() {
    when(assessmentGatewayService.getDetail(1L)).thenThrow(new RuntimeException("详情查询失败"));

    ResponseEntity<Map<String, Object>> response = controller.getDetail(1L);

    assertEquals(400, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(false, response.getBody().get("success"));
    assertEquals("详情查询失败", response.getBody().get("message"));

    verify(assessmentGatewayService).getDetail(1L);
  }

  @Test
  void submit_success() {
    UserContext.set(new LoginUser("s001", "student"));

    AssessmentSubmitRequest request = mock(AssessmentSubmitRequest.class);

    ResponseEntity<Map<String, Object>> response = controller.submit(request);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(true, response.getBody().get("success"));
    assertEquals("提交成功", response.getBody().get("message"));
    assertTrue(response.getBody().containsKey("data"));

    verify(request).setStudentId("s001");
    verify(assessmentGatewayService).submit(request);
  }

  @Test
  void submit_whenException_shouldReturnBadRequest() {
    UserContext.set(new LoginUser("s001", "student"));

    AssessmentSubmitRequest request = mock(AssessmentSubmitRequest.class);
    doThrow(new RuntimeException("提交失败")).when(request).setStudentId("s001");

    ResponseEntity<Map<String, Object>> response = controller.submit(request);

    assertEquals(400, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(false, response.getBody().get("success"));
    assertEquals("提交失败", response.getBody().get("message"));

    verify(request).setStudentId("s001");
    verify(assessmentGatewayService, never()).submit(request);
  }

  @Test
  void getRecords_success() {
    UserContext.set(new LoginUser("s001", "student"));

    ResponseEntity<Map<String, Object>> response = controller.getRecords();

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(true, response.getBody().get("success"));
    assertEquals("查询成功", response.getBody().get("message"));
    assertTrue(response.getBody().containsKey("data"));

    verify(assessmentGatewayService).getRecords("s001");
  }

  @Test
  void getRecords_whenException_shouldReturnBadRequest() {
    UserContext.set(new LoginUser("s001", "student"));
    when(assessmentGatewayService.getRecords("s001")).thenThrow(new RuntimeException("记录查询失败"));

    ResponseEntity<Map<String, Object>> response = controller.getRecords();

    assertEquals(400, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(false, response.getBody().get("success"));
    assertEquals("记录查询失败", response.getBody().get("message"));

    verify(assessmentGatewayService).getRecords("s001");
  }
}