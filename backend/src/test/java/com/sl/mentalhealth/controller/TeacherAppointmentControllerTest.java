package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.TeacherAppointmentQueryRequest;
import com.sl.mentalhealth.dto.TeacherAppointmentUpdateStatusRequest;
import com.sl.mentalhealth.dto.TeacherAssessmentRecordQueryRequest;
import com.sl.mentalhealth.kafka.message.TeacherAppointmentResponseMessage;
import com.sl.mentalhealth.service.TeacherAppointmentGatewayService;
import com.sl.mentalhealth.vo.TeacherAppointmentVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherAppointmentControllerTest {

  @Mock
  private TeacherAppointmentGatewayService teacherAppointmentGatewayService;

  @InjectMocks
  private TeacherAppointmentController controller;

  @Test
  void query_success() {
    TeacherAppointmentQueryRequest request = mock(TeacherAppointmentQueryRequest.class);
    TeacherAppointmentResponseMessage response = mock(TeacherAppointmentResponseMessage.class);
    Object data = Collections.emptyList();

    when(response.isSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("查询成功");
    doReturn(data).when(response).getAppointmentList();
    when(teacherAppointmentGatewayService.query(request)).thenReturn(response);

    Result<?> result = controller.query(request);

    assertNotNull(result);
    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertEquals(data, result.getData());
  }

  @Test
  void query_whenFailed_shouldReturnError() {
    TeacherAppointmentQueryRequest request = mock(TeacherAppointmentQueryRequest.class);
    TeacherAppointmentResponseMessage response = mock(TeacherAppointmentResponseMessage.class);

    when(response.isSuccess()).thenReturn(false);
    when(response.getMessage()).thenReturn("查询失败");
    when(teacherAppointmentGatewayService.query(request)).thenReturn(response);

    Result<?> result = controller.query(request);

    assertNotNull(result);
    assertEquals(500, result.getCode());
    assertEquals("查询失败", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void record_success() {
    TeacherAppointmentQueryRequest request = mock(TeacherAppointmentQueryRequest.class);
    TeacherAppointmentResponseMessage response = mock(TeacherAppointmentResponseMessage.class);
    Object data = Collections.emptyList();

    when(response.isSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("查询成功");
    doReturn(data).when(response).getAppointmentList();
    when(teacherAppointmentGatewayService.record(request)).thenReturn(response);

    Result<?> result = controller.record(request);

    assertNotNull(result);
    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertEquals(data, result.getData());
  }

  @Test
  void record_whenFailed_shouldReturnError() {
    TeacherAppointmentQueryRequest request = mock(TeacherAppointmentQueryRequest.class);
    TeacherAppointmentResponseMessage response = mock(TeacherAppointmentResponseMessage.class);

    when(response.isSuccess()).thenReturn(false);
    when(response.getMessage()).thenReturn("查询失败");
    when(teacherAppointmentGatewayService.record(request)).thenReturn(response);

    Result<?> result = controller.record(request);

    assertNotNull(result);
    assertEquals(500, result.getCode());
    assertEquals("查询失败", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void updateStatus_success() {
    TeacherAppointmentUpdateStatusRequest request = mock(TeacherAppointmentUpdateStatusRequest.class);
    TeacherAppointmentResponseMessage response = mock(TeacherAppointmentResponseMessage.class);
    TeacherAppointmentVO data = mock(TeacherAppointmentVO.class);

    when(response.isSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("更新成功");
    doReturn(data).when(response).getAppointmentData();
    when(teacherAppointmentGatewayService.updateStatus(request)).thenReturn(response);

    Result<?> result = controller.updateStatus(request);

    assertNotNull(result);
    assertEquals(200, result.getCode());
    assertEquals("更新成功", result.getMessage());
    assertSame(data, result.getData());
  }

  @Test
  void updateStatus_whenFailed_shouldReturnError() {
    TeacherAppointmentUpdateStatusRequest request = mock(TeacherAppointmentUpdateStatusRequest.class);
    TeacherAppointmentResponseMessage response = mock(TeacherAppointmentResponseMessage.class);

    when(response.isSuccess()).thenReturn(false);
    when(response.getMessage()).thenReturn("更新失败");
    when(teacherAppointmentGatewayService.updateStatus(request)).thenReturn(response);

    Result<?> result = controller.updateStatus(request);

    assertNotNull(result);
    assertEquals(500, result.getCode());
    assertEquals("更新失败", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void assessmentRecord_success() {
    TeacherAssessmentRecordQueryRequest request = mock(TeacherAssessmentRecordQueryRequest.class);
    TeacherAppointmentResponseMessage response = mock(TeacherAppointmentResponseMessage.class);
    Object data = Collections.emptyList();

    when(response.isSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("查询成功");
    doReturn(data).when(response).getAssessmentRecordList();
    when(teacherAppointmentGatewayService.assessmentRecord(request)).thenReturn(response);

    Result<?> result = controller.assessmentRecord(request);

    assertNotNull(result);
    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertEquals(data, result.getData());
  }

  @Test
  void assessmentRecord_whenFailed_shouldReturnError() {
    TeacherAssessmentRecordQueryRequest request = mock(TeacherAssessmentRecordQueryRequest.class);
    TeacherAppointmentResponseMessage response = mock(TeacherAppointmentResponseMessage.class);

    when(response.isSuccess()).thenReturn(false);
    when(response.getMessage()).thenReturn("查询失败");
    when(teacherAppointmentGatewayService.assessmentRecord(request)).thenReturn(response);

    Result<?> result = controller.assessmentRecord(request);

    assertNotNull(result);
    assertEquals(500, result.getCode());
    assertEquals("查询失败", result.getMessage());
    assertNull(result.getData());
  }
}