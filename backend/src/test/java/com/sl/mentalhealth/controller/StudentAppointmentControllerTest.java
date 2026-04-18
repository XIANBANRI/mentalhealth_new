package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.AppointmentCancelRequest;
import com.sl.mentalhealth.dto.AppointmentCreateRequest;
import com.sl.mentalhealth.dto.TeacherAppointmentAuditRequest;
import com.sl.mentalhealth.service.AppointmentGatewayService;
import com.sl.mentalhealth.vo.AppointmentVO;
import com.sl.mentalhealth.vo.AvailableAppointmentVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentAppointmentControllerTest {

  @Mock
  private AppointmentGatewayService appointmentGatewayService;

  @InjectMocks
  private StudentAppointmentController controller;

  @Test
  void available_success() {
    List<AvailableAppointmentVO> list = Arrays.asList(
        mock(AvailableAppointmentVO.class),
        mock(AvailableAppointmentVO.class)
    );
    when(appointmentGatewayService.studentAvailable("2026-04-05")).thenReturn(list);

    Result<List<AvailableAppointmentVO>> result = controller.available("2026-04-05");

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(list, result.getData());
  }

  @Test
  void create_success() {
    AppointmentCreateRequest request = mock(AppointmentCreateRequest.class);
    when(appointmentGatewayService.studentCreate(request)).thenReturn(1001L);

    Result<Long> result = controller.create(request);

    assertEquals(200, result.getCode());
    assertEquals("预约提交成功", result.getMessage());
    assertEquals(1001L, result.getData());
  }

  @Test
  void my_success() {
    List<AppointmentVO> list = Collections.singletonList(mock(AppointmentVO.class));
    when(appointmentGatewayService.studentMy("s001")).thenReturn(list);

    Result<List<AppointmentVO>> result = controller.my("s001");

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(list, result.getData());
  }

  @Test
  void cancel_success() {
    AppointmentCancelRequest request = mock(AppointmentCancelRequest.class);

    Result<Void> result = controller.cancel(request);

    assertEquals(200, result.getCode());
    assertEquals("取消成功", result.getMessage());
    assertNull(result.getData());

    verify(appointmentGatewayService).studentCancel(request);
  }

  @Test
  void teacherList_success() {
    List<AppointmentVO> list = Collections.singletonList(mock(AppointmentVO.class));
    when(appointmentGatewayService.teacherList("t001", "APPROVED", "2026-04-05")).thenReturn(list);

    Result<List<AppointmentVO>> result =
        controller.teacherList("t001", "APPROVED", "2026-04-05");

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(list, result.getData());
  }

  @Test
  void teacherApprove_success() {
    TeacherAppointmentAuditRequest request = mock(TeacherAppointmentAuditRequest.class);

    Result<Void> result = controller.teacherApprove(request);

    assertEquals(200, result.getCode());
    assertEquals("通过成功", result.getMessage());
    assertNull(result.getData());

    verify(appointmentGatewayService).teacherApprove(request);
  }

  @Test
  void teacherReject_success() {
    TeacherAppointmentAuditRequest request = mock(TeacherAppointmentAuditRequest.class);

    Result<Void> result = controller.teacherReject(request);

    assertEquals(200, result.getCode());
    assertEquals("拒绝成功", result.getMessage());
    assertNull(result.getData());

    verify(appointmentGatewayService).teacherReject(request);
  }

  @Test
  void teacherComplete_success() {
    TeacherAppointmentAuditRequest request = mock(TeacherAppointmentAuditRequest.class);

    Result<Void> result = controller.teacherComplete(request);

    assertEquals(200, result.getCode());
    assertEquals("完成成功", result.getMessage());
    assertNull(result.getData());

    verify(appointmentGatewayService).teacherComplete(request);
  }
}