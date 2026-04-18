package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.TeacherScheduleDeleteRequest;
import com.sl.mentalhealth.dto.TeacherScheduleQueryRequest;
import com.sl.mentalhealth.dto.TeacherScheduleSaveRequest;
import com.sl.mentalhealth.kafka.message.TeacherScheduleResponseMessage;
import com.sl.mentalhealth.service.TeacherScheduleGatewayService;
import com.sl.mentalhealth.vo.TeacherScheduleVO;
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
class TeacherScheduleControllerTest {

  @Mock
  private TeacherScheduleGatewayService teacherScheduleGatewayService;

  @InjectMocks
  private TeacherScheduleController controller;

  @Test
  void query_success() {
    TeacherScheduleQueryRequest request = mock(TeacherScheduleQueryRequest.class);
    TeacherScheduleResponseMessage response = mock(TeacherScheduleResponseMessage.class);
    Object data = Collections.emptyList();

    when(response.isSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("查询成功");
    doReturn(data).when(response).getList();
    when(teacherScheduleGatewayService.query(request)).thenReturn(response);

    Result<?> result = controller.query(request);

    assertNotNull(result);
    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertEquals(data, result.getData());
  }

  @Test
  void query_whenFailed_shouldReturnError() {
    TeacherScheduleQueryRequest request = mock(TeacherScheduleQueryRequest.class);
    TeacherScheduleResponseMessage response = mock(TeacherScheduleResponseMessage.class);

    when(response.isSuccess()).thenReturn(false);
    when(response.getMessage()).thenReturn("查询失败");
    when(teacherScheduleGatewayService.query(request)).thenReturn(response);

    Result<?> result = controller.query(request);

    assertNotNull(result);
    assertEquals(500, result.getCode());
    assertEquals("查询失败", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void add_success() {
    TeacherScheduleSaveRequest request = mock(TeacherScheduleSaveRequest.class);
    TeacherScheduleResponseMessage response = mock(TeacherScheduleResponseMessage.class);
    TeacherScheduleVO data = mock(TeacherScheduleVO.class);

    when(response.isSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("新增成功");
    doReturn(data).when(response).getData();
    when(teacherScheduleGatewayService.add(request)).thenReturn(response);

    Result<?> result = controller.add(request);

    assertNotNull(result);
    assertEquals(200, result.getCode());
    assertEquals("新增成功", result.getMessage());
    assertSame(data, result.getData());
  }

  @Test
  void add_whenFailed_shouldReturnError() {
    TeacherScheduleSaveRequest request = mock(TeacherScheduleSaveRequest.class);
    TeacherScheduleResponseMessage response = mock(TeacherScheduleResponseMessage.class);

    when(response.isSuccess()).thenReturn(false);
    when(response.getMessage()).thenReturn("新增失败");
    when(teacherScheduleGatewayService.add(request)).thenReturn(response);

    Result<?> result = controller.add(request);

    assertNotNull(result);
    assertEquals(500, result.getCode());
    assertEquals("新增失败", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void update_success() {
    TeacherScheduleSaveRequest request = mock(TeacherScheduleSaveRequest.class);
    TeacherScheduleResponseMessage response = mock(TeacherScheduleResponseMessage.class);
    TeacherScheduleVO data = mock(TeacherScheduleVO.class);

    when(response.isSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("修改成功");
    doReturn(data).when(response).getData();
    when(teacherScheduleGatewayService.update(request)).thenReturn(response);

    Result<?> result = controller.update(request);

    assertNotNull(result);
    assertEquals(200, result.getCode());
    assertEquals("修改成功", result.getMessage());
    assertSame(data, result.getData());
  }

  @Test
  void update_whenFailed_shouldReturnError() {
    TeacherScheduleSaveRequest request = mock(TeacherScheduleSaveRequest.class);
    TeacherScheduleResponseMessage response = mock(TeacherScheduleResponseMessage.class);

    when(response.isSuccess()).thenReturn(false);
    when(response.getMessage()).thenReturn("修改失败");
    when(teacherScheduleGatewayService.update(request)).thenReturn(response);

    Result<?> result = controller.update(request);

    assertNotNull(result);
    assertEquals(500, result.getCode());
    assertEquals("修改失败", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void delete_success() {
    TeacherScheduleDeleteRequest request = mock(TeacherScheduleDeleteRequest.class);
    TeacherScheduleResponseMessage response = mock(TeacherScheduleResponseMessage.class);

    when(response.isSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("删除成功");
    when(teacherScheduleGatewayService.delete(request)).thenReturn(response);

    Result<?> result = controller.delete(request);

    assertNotNull(result);
    assertEquals(200, result.getCode());
    assertEquals("删除成功", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void delete_whenFailed_shouldReturnError() {
    TeacherScheduleDeleteRequest request = mock(TeacherScheduleDeleteRequest.class);
    TeacherScheduleResponseMessage response = mock(TeacherScheduleResponseMessage.class);

    when(response.isSuccess()).thenReturn(false);
    when(response.getMessage()).thenReturn("删除失败");
    when(teacherScheduleGatewayService.delete(request)).thenReturn(response);

    Result<?> result = controller.delete(request);

    assertNotNull(result);
    assertEquals(500, result.getCode());
    assertEquals("删除失败", result.getMessage());
    assertNull(result.getData());
  }
}