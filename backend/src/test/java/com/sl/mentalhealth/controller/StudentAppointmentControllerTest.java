package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.config.LoginUser;
import com.sl.mentalhealth.config.UserContext;
import com.sl.mentalhealth.dto.AppointmentCancelRequest;
import com.sl.mentalhealth.dto.AppointmentCreateRequest;
import com.sl.mentalhealth.service.AppointmentGatewayService;
import com.sl.mentalhealth.vo.AppointmentVO;
import com.sl.mentalhealth.vo.AvailableAppointmentVO;
import org.junit.jupiter.api.AfterEach;
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

  @AfterEach
  void tearDown() {
    UserContext.clear();
  }

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
    UserContext.set(new LoginUser("s001", "student"));

    AppointmentCreateRequest request = mock(AppointmentCreateRequest.class);
    when(appointmentGatewayService.studentCreate(request)).thenReturn(1001L);

    Result<Long> result = controller.create(request);

    assertEquals(200, result.getCode());
    assertEquals("预约提交成功", result.getMessage());
    assertEquals(1001L, result.getData());

    verify(request).setStudentId("s001");
    verify(appointmentGatewayService).studentCreate(request);
  }

  @Test
  void my_success() {
    UserContext.set(new LoginUser("s001", "student"));

    List<AppointmentVO> list = Collections.singletonList(mock(AppointmentVO.class));
    when(appointmentGatewayService.studentMy("s001")).thenReturn(list);

    Result<List<AppointmentVO>> result = controller.my();

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(list, result.getData());
  }

  @Test
  void cancel_success() {
    UserContext.set(new LoginUser("s001", "student"));

    AppointmentCancelRequest request = mock(AppointmentCancelRequest.class);

    Result<Void> result = controller.cancel(request);

    assertEquals(200, result.getCode());
    assertEquals("取消成功", result.getMessage());
    assertNull(result.getData());

    verify(request).setStudentId("s001");
    verify(appointmentGatewayService).studentCancel(request);
  }
}