package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.dto.AppointmentCancelRequest;
import com.sl.mentalhealth.dto.AppointmentCreateRequest;
import com.sl.mentalhealth.dto.TeacherAppointmentAuditRequest;
import com.sl.mentalhealth.kafka.AppointmentRequestProducer;
import com.sl.mentalhealth.kafka.message.AppointmentRequestMessage;
import com.sl.mentalhealth.kafka.message.AppointmentResponseMessage;
import com.sl.mentalhealth.vo.AppointmentVO;
import com.sl.mentalhealth.vo.AvailableAppointmentVO;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppointmentGatewayServiceTest {

  @Mock
  private AppointmentRequestProducer appointmentRequestProducer;

  @Mock
  private PendingAppointmentService pendingAppointmentService;

  @InjectMocks
  private AppointmentGatewayService service;

  @Test
  void studentAvailable_success() {
    List<AvailableAppointmentVO> expected =
        Collections.singletonList(org.mockito.Mockito.mock(AvailableAppointmentVO.class));

    AppointmentResponseMessage response = new AppointmentResponseMessage();
    response.setSuccess(true);
    response.setAvailableTeachers(expected);

    CompletableFuture<AppointmentResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAppointmentService.createFuture(anyString())).thenReturn(future);

    List<AvailableAppointmentVO> result = service.studentAvailable("2026-04-05");

    assertSame(expected, result);

    ArgumentCaptor<AppointmentRequestMessage> captor =
        ArgumentCaptor.forClass(AppointmentRequestMessage.class);
    verify(appointmentRequestProducer).send(captor.capture());
    AppointmentRequestMessage sent = captor.getValue();

    assertEquals("STUDENT_AVAILABLE", sent.getOperation());
    assertEquals("2026-04-05", sent.getDate());
    verify(pendingAppointmentService).createFuture(sent.getRequestId());
  }

  @Test
  void studentCreate_success() {
    AppointmentCreateRequest req = org.mockito.Mockito.mock(AppointmentCreateRequest.class);
    when(req.getStudentId()).thenReturn("s001");
    when(req.getScheduleId()).thenReturn(10L);
    when(req.getAppointmentDate()).thenReturn("2026-04-08");
    when(req.getPurpose()).thenReturn("咨询");
    when(req.getRemark()).thenReturn("备注");

    AppointmentResponseMessage response = new AppointmentResponseMessage();
    response.setSuccess(true);
    response.setAppointmentId(99L);

    CompletableFuture<AppointmentResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAppointmentService.createFuture(anyString())).thenReturn(future);

    Long result = service.studentCreate(req);

    assertEquals(99L, result);

    ArgumentCaptor<AppointmentRequestMessage> captor =
        ArgumentCaptor.forClass(AppointmentRequestMessage.class);
    verify(appointmentRequestProducer).send(captor.capture());
    AppointmentRequestMessage sent = captor.getValue();

    assertEquals("STUDENT_CREATE", sent.getOperation());
    assertEquals("s001", sent.getStudentId());
    assertEquals(10L, sent.getScheduleId());
    assertEquals("2026-04-08", sent.getAppointmentDate());
    assertEquals("咨询", sent.getPurpose());
    assertEquals("备注", sent.getRemark());
  }

  @Test
  void studentMy_success() {
    List<AppointmentVO> expected =
        Collections.singletonList(org.mockito.Mockito.mock(AppointmentVO.class));

    AppointmentResponseMessage response = new AppointmentResponseMessage();
    response.setSuccess(true);
    response.setAppointmentList(expected);

    CompletableFuture<AppointmentResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAppointmentService.createFuture(anyString())).thenReturn(future);

    List<AppointmentVO> result = service.studentMy("s001");

    assertSame(expected, result);

    ArgumentCaptor<AppointmentRequestMessage> captor =
        ArgumentCaptor.forClass(AppointmentRequestMessage.class);
    verify(appointmentRequestProducer).send(captor.capture());
    AppointmentRequestMessage sent = captor.getValue();

    assertEquals("STUDENT_MY", sent.getOperation());
    assertEquals("s001", sent.getStudentId());
  }

  @Test
  void studentCancel_success() {
    AppointmentCancelRequest req = org.mockito.Mockito.mock(AppointmentCancelRequest.class);
    when(req.getStudentId()).thenReturn("s001");
    when(req.getAppointmentId()).thenReturn(100L);

    AppointmentResponseMessage response = new AppointmentResponseMessage();
    response.setSuccess(true);

    CompletableFuture<AppointmentResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAppointmentService.createFuture(anyString())).thenReturn(future);

    service.studentCancel(req);

    ArgumentCaptor<AppointmentRequestMessage> captor =
        ArgumentCaptor.forClass(AppointmentRequestMessage.class);
    verify(appointmentRequestProducer).send(captor.capture());
    AppointmentRequestMessage sent = captor.getValue();

    assertEquals("STUDENT_CANCEL", sent.getOperation());
    assertEquals("s001", sent.getStudentId());
    assertEquals(100L, sent.getAppointmentId());
  }

  @Test
  void teacherList_success() {
    List<AppointmentVO> expected =
        Collections.singletonList(org.mockito.Mockito.mock(AppointmentVO.class));

    AppointmentResponseMessage response = new AppointmentResponseMessage();
    response.setSuccess(true);
    response.setAppointmentList(expected);

    CompletableFuture<AppointmentResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAppointmentService.createFuture(anyString())).thenReturn(future);

    List<AppointmentVO> result = service.teacherList("t001", "PENDING", "2026-04-05");

    assertSame(expected, result);

    ArgumentCaptor<AppointmentRequestMessage> captor =
        ArgumentCaptor.forClass(AppointmentRequestMessage.class);
    verify(appointmentRequestProducer).send(captor.capture());
    AppointmentRequestMessage sent = captor.getValue();

    assertEquals("TEACHER_LIST", sent.getOperation());
    assertEquals("t001", sent.getTeacherAccount());
    assertEquals("PENDING", sent.getStatus());
    assertEquals("2026-04-05", sent.getDate());
  }

  @Test
  void teacherApprove_success() {
    TeacherAppointmentAuditRequest req =
        org.mockito.Mockito.mock(TeacherAppointmentAuditRequest.class);
    when(req.getAppointmentId()).thenReturn(1L);
    when(req.getTeacherAccount()).thenReturn("t001");
    when(req.getTeacherReply()).thenReturn("同意");

    AppointmentResponseMessage response = new AppointmentResponseMessage();
    response.setSuccess(true);

    CompletableFuture<AppointmentResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAppointmentService.createFuture(anyString())).thenReturn(future);

    service.teacherApprove(req);

    ArgumentCaptor<AppointmentRequestMessage> captor =
        ArgumentCaptor.forClass(AppointmentRequestMessage.class);
    verify(appointmentRequestProducer).send(captor.capture());
    AppointmentRequestMessage sent = captor.getValue();

    assertEquals("TEACHER_APPROVE", sent.getOperation());
    assertEquals(1L, sent.getAppointmentId());
    assertEquals("t001", sent.getTeacherAccount());
    assertEquals("同意", sent.getTeacherReply());
  }

  @Test
  void teacherReject_success_setsRejectReason() {
    TeacherAppointmentAuditRequest req =
        org.mockito.Mockito.mock(TeacherAppointmentAuditRequest.class);
    when(req.getAppointmentId()).thenReturn(2L);
    when(req.getTeacherAccount()).thenReturn("t001");
    when(req.getTeacherReply()).thenReturn("时间冲突");

    AppointmentResponseMessage response = new AppointmentResponseMessage();
    response.setSuccess(true);

    CompletableFuture<AppointmentResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAppointmentService.createFuture(anyString())).thenReturn(future);

    service.teacherReject(req);

    ArgumentCaptor<AppointmentRequestMessage> captor =
        ArgumentCaptor.forClass(AppointmentRequestMessage.class);
    verify(appointmentRequestProducer).send(captor.capture());
    AppointmentRequestMessage sent = captor.getValue();

    assertEquals("TEACHER_REJECT", sent.getOperation());
    assertEquals("时间冲突", sent.getRejectReason());
  }

  @Test
  void teacherComplete_success() {
    TeacherAppointmentAuditRequest req =
        org.mockito.Mockito.mock(TeacherAppointmentAuditRequest.class);
    when(req.getAppointmentId()).thenReturn(3L);
    when(req.getTeacherAccount()).thenReturn("t001");
    when(req.getTeacherReply()).thenReturn("已完成");

    AppointmentResponseMessage response = new AppointmentResponseMessage();
    response.setSuccess(true);

    CompletableFuture<AppointmentResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAppointmentService.createFuture(anyString())).thenReturn(future);

    service.teacherComplete(req);

    ArgumentCaptor<AppointmentRequestMessage> captor =
        ArgumentCaptor.forClass(AppointmentRequestMessage.class);
    verify(appointmentRequestProducer).send(captor.capture());
    AppointmentRequestMessage sent = captor.getValue();

    assertEquals("TEACHER_COMPLETE", sent.getOperation());
    assertEquals("已完成", sent.getTeacherReply());
  }

  @Test
  void sendAndWait_failResponse_throwsException() {
    AppointmentResponseMessage response = new AppointmentResponseMessage();
    response.setSuccess(false);
    response.setMessage("处理失败");

    CompletableFuture<AppointmentResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAppointmentService.createFuture(anyString())).thenReturn(future);

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> service.studentAvailable("2026-04-05"));

    assertEquals("处理失败", ex.getMessage());
    verify(pendingAppointmentService, never()).remove(anyString());
  }

  @Test
  void sendAndWait_futureThrows_removesPendingAndWrapsException() {
    CompletableFuture<AppointmentResponseMessage> future = new CompletableFuture<>();
    future.completeExceptionally(new RuntimeException("boom"));
    when(pendingAppointmentService.createFuture(anyString())).thenReturn(future);

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> service.studentAvailable("2026-04-05"));

    assertEquals("预约服务处理失败：java.lang.RuntimeException: boom", ex.getMessage());

    ArgumentCaptor<AppointmentRequestMessage> captor =
        ArgumentCaptor.forClass(AppointmentRequestMessage.class);
    verify(appointmentRequestProducer).send(captor.capture());
    verify(pendingAppointmentService).remove(captor.getValue().getRequestId());
  }
}