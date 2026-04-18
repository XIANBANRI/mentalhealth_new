package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AppointmentRequestMessage;
import com.sl.mentalhealth.kafka.message.AppointmentResponseMessage;
import com.sl.mentalhealth.service.LocalAppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentRequestConsumerTest {

  @Mock
  private LocalAppointmentService localAppointmentService;

  @Mock
  private AppointmentResponseProducer appointmentResponseProducer;

  @InjectMocks
  private AppointmentRequestConsumer consumer;

  @Test
  void consume_studentAvailable_success() {
    AppointmentRequestMessage request = mock(AppointmentRequestMessage.class);
    when(request.getRequestId()).thenReturn("req-001");
    when(request.getOperation()).thenReturn("STUDENT_AVAILABLE");
    when(localAppointmentService.studentAvailable(any())).thenReturn(Collections.emptyList());

    consumer.consume(request);

    verify(localAppointmentService, times(1)).studentAvailable(any());
    AppointmentResponseMessage sent = captureResponse();

    assertEquals("req-001", ReflectionTestUtils.getField(sent, "requestId"));
    assertEquals("STUDENT_AVAILABLE", ReflectionTestUtils.getField(sent, "operation"));
    assertEquals(true, ReflectionTestUtils.getField(sent, "success"));
    assertEquals("查询成功", ReflectionTestUtils.getField(sent, "message"));
  }

  @Test
  void consume_studentCreate_success() {
    AppointmentRequestMessage request = mock(AppointmentRequestMessage.class);
    when(request.getRequestId()).thenReturn("req-002");
    when(request.getOperation()).thenReturn("STUDENT_CREATE");
    when(localAppointmentService.studentCreate(any(), any(), any(), any(), any()))
        .thenReturn(123L);

    consumer.consume(request);

    verify(localAppointmentService, times(1))
        .studentCreate(any(), any(), any(), any(), any());
    AppointmentResponseMessage sent = captureResponse();

    assertEquals("req-002", ReflectionTestUtils.getField(sent, "requestId"));
    assertEquals("STUDENT_CREATE", ReflectionTestUtils.getField(sent, "operation"));
    assertEquals(true, ReflectionTestUtils.getField(sent, "success"));
    assertEquals("预约提交成功", ReflectionTestUtils.getField(sent, "message"));
    assertEquals(123L, ReflectionTestUtils.getField(sent, "appointmentId"));
  }

  @Test
  void consume_studentMy_success() {
    AppointmentRequestMessage request = mock(AppointmentRequestMessage.class);
    when(request.getRequestId()).thenReturn("req-003");
    when(request.getOperation()).thenReturn("STUDENT_MY");
    when(localAppointmentService.studentMy(any())).thenReturn(Collections.emptyList());

    consumer.consume(request);

    verify(localAppointmentService, times(1)).studentMy(any());
    AppointmentResponseMessage sent = captureResponse();

    assertEquals("req-003", ReflectionTestUtils.getField(sent, "requestId"));
    assertEquals("STUDENT_MY", ReflectionTestUtils.getField(sent, "operation"));
    assertEquals(true, ReflectionTestUtils.getField(sent, "success"));
    assertEquals("查询成功", ReflectionTestUtils.getField(sent, "message"));
  }

  @Test
  void consume_studentCancel_success() {
    AppointmentRequestMessage request = mock(AppointmentRequestMessage.class);
    when(request.getRequestId()).thenReturn("req-004");
    when(request.getOperation()).thenReturn("STUDENT_CANCEL");

    consumer.consume(request);

    verify(localAppointmentService, times(1)).studentCancel(any(), any());
    AppointmentResponseMessage sent = captureResponse();

    assertEquals("req-004", ReflectionTestUtils.getField(sent, "requestId"));
    assertEquals("STUDENT_CANCEL", ReflectionTestUtils.getField(sent, "operation"));
    assertEquals(true, ReflectionTestUtils.getField(sent, "success"));
    assertEquals("取消成功", ReflectionTestUtils.getField(sent, "message"));
  }

  @Test
  void consume_teacherList_success() {
    AppointmentRequestMessage request = mock(AppointmentRequestMessage.class);
    when(request.getRequestId()).thenReturn("req-005");
    when(request.getOperation()).thenReturn("TEACHER_LIST");
    when(localAppointmentService.teacherList(any(), any(), any()))
        .thenReturn(Collections.emptyList());

    consumer.consume(request);

    verify(localAppointmentService, times(1)).teacherList(any(), any(), any());
    AppointmentResponseMessage sent = captureResponse();

    assertEquals("req-005", ReflectionTestUtils.getField(sent, "requestId"));
    assertEquals("TEACHER_LIST", ReflectionTestUtils.getField(sent, "operation"));
    assertEquals(true, ReflectionTestUtils.getField(sent, "success"));
    assertEquals("查询成功", ReflectionTestUtils.getField(sent, "message"));
  }

  @Test
  void consume_teacherApprove_success() {
    AppointmentRequestMessage request = mock(AppointmentRequestMessage.class);
    when(request.getRequestId()).thenReturn("req-006");
    when(request.getOperation()).thenReturn("TEACHER_APPROVE");

    consumer.consume(request);

    verify(localAppointmentService, times(1)).teacherApprove(any(), any(), any());
    AppointmentResponseMessage sent = captureResponse();

    assertEquals("req-006", ReflectionTestUtils.getField(sent, "requestId"));
    assertEquals("TEACHER_APPROVE", ReflectionTestUtils.getField(sent, "operation"));
    assertEquals(true, ReflectionTestUtils.getField(sent, "success"));
    assertEquals("通过成功", ReflectionTestUtils.getField(sent, "message"));
  }

  @Test
  void consume_teacherReject_success() {
    AppointmentRequestMessage request = mock(AppointmentRequestMessage.class);
    when(request.getRequestId()).thenReturn("req-007");
    when(request.getOperation()).thenReturn("TEACHER_REJECT");

    consumer.consume(request);

    verify(localAppointmentService, times(1)).teacherReject(any(), any(), any());
    AppointmentResponseMessage sent = captureResponse();

    assertEquals("req-007", ReflectionTestUtils.getField(sent, "requestId"));
    assertEquals("TEACHER_REJECT", ReflectionTestUtils.getField(sent, "operation"));
    assertEquals(true, ReflectionTestUtils.getField(sent, "success"));
    assertEquals("拒绝成功", ReflectionTestUtils.getField(sent, "message"));
  }

  @Test
  void consume_teacherComplete_success() {
    AppointmentRequestMessage request = mock(AppointmentRequestMessage.class);
    when(request.getRequestId()).thenReturn("req-008");
    when(request.getOperation()).thenReturn("TEACHER_COMPLETE");

    consumer.consume(request);

    verify(localAppointmentService, times(1)).teacherComplete(any(), any(), any());
    AppointmentResponseMessage sent = captureResponse();

    assertEquals("req-008", ReflectionTestUtils.getField(sent, "requestId"));
    assertEquals("TEACHER_COMPLETE", ReflectionTestUtils.getField(sent, "operation"));
    assertEquals(true, ReflectionTestUtils.getField(sent, "success"));
    assertEquals("完成成功", ReflectionTestUtils.getField(sent, "message"));
  }

  @Test
  void consume_unknownOperation_fail() {
    AppointmentRequestMessage request = mock(AppointmentRequestMessage.class);
    when(request.getRequestId()).thenReturn("req-009");
    when(request.getOperation()).thenReturn("UNKNOWN_OP");

    consumer.consume(request);

    verifyNoInteractions(localAppointmentService);
    AppointmentResponseMessage sent = captureResponse();

    assertEquals("req-009", ReflectionTestUtils.getField(sent, "requestId"));
    assertEquals("UNKNOWN_OP", ReflectionTestUtils.getField(sent, "operation"));
    assertEquals(false, ReflectionTestUtils.getField(sent, "success"));
    assertEquals("未知操作：UNKNOWN_OP", ReflectionTestUtils.getField(sent, "message"));
  }

  @Test
  void consume_exception_fail() {
    AppointmentRequestMessage request = mock(AppointmentRequestMessage.class);
    when(request.getRequestId()).thenReturn("req-010");
    when(request.getOperation()).thenReturn("STUDENT_AVAILABLE");
    when(localAppointmentService.studentAvailable(any()))
        .thenThrow(new RuntimeException("查询失败"));

    consumer.consume(request);

    verify(localAppointmentService, times(1)).studentAvailable(any());
    AppointmentResponseMessage sent = captureResponse();

    assertEquals("req-010", ReflectionTestUtils.getField(sent, "requestId"));
    assertEquals("STUDENT_AVAILABLE", ReflectionTestUtils.getField(sent, "operation"));
    assertEquals(false, ReflectionTestUtils.getField(sent, "success"));
    assertEquals("查询失败", ReflectionTestUtils.getField(sent, "message"));
  }

  private AppointmentResponseMessage captureResponse() {
    ArgumentCaptor<AppointmentResponseMessage> captor =
        ArgumentCaptor.forClass(AppointmentResponseMessage.class);
    verify(appointmentResponseProducer, atLeastOnce()).send(captor.capture());
    return captor.getValue();
  }
}