package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.dto.TeacherScheduleDeleteRequest;
import com.sl.mentalhealth.dto.TeacherScheduleQueryRequest;
import com.sl.mentalhealth.dto.TeacherScheduleSaveRequest;
import com.sl.mentalhealth.kafka.message.TeacherScheduleRequestMessage;
import com.sl.mentalhealth.kafka.message.TeacherScheduleResponseMessage;
import com.sl.mentalhealth.service.LocalTeacherScheduleService;
import com.sl.mentalhealth.vo.TeacherScheduleVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherScheduleRequestConsumerTest {

  @Mock
  private LocalTeacherScheduleService localTeacherScheduleService;

  @Mock
  private TeacherScheduleResponseProducer teacherScheduleResponseProducer;

  @InjectMocks
  private TeacherScheduleRequestConsumer consumer;

  @Test
  void consume_query_success() {
    TeacherScheduleRequestMessage message = mock(TeacherScheduleRequestMessage.class);
    TeacherScheduleQueryRequest request = mock(TeacherScheduleQueryRequest.class);
    List<TeacherScheduleVO> list = Collections.singletonList(mock(TeacherScheduleVO.class));

    when(message.getRequestId()).thenReturn("req-001");
    when(message.getAction()).thenReturn("QUERY");
    when(message.getQueryRequest()).thenReturn(request);
    when(localTeacherScheduleService.query(request)).thenReturn(list);

    consumer.consume(message);

    verify(localTeacherScheduleService, times(1)).query(request);

    TeacherScheduleResponseMessage response = captureResponse();
    assertEquals("req-001", response.getRequestId());
    assertEquals(Boolean.TRUE, ReflectionTestUtils.getField(response, "success"));
    assertEquals("查询成功", ReflectionTestUtils.getField(response, "message"));
    assertEquals(list, ReflectionTestUtils.getField(response, "list"));
    assertNull(ReflectionTestUtils.getField(response, "data"));
  }

  @Test
  void consume_add_success() {
    TeacherScheduleRequestMessage message = mock(TeacherScheduleRequestMessage.class);
    TeacherScheduleSaveRequest request = mock(TeacherScheduleSaveRequest.class);
    TeacherScheduleVO data = mock(TeacherScheduleVO.class);

    when(message.getRequestId()).thenReturn("req-002");
    when(message.getAction()).thenReturn("ADD");
    when(message.getSaveRequest()).thenReturn(request);
    when(localTeacherScheduleService.add(request)).thenReturn(data);

    consumer.consume(message);

    verify(localTeacherScheduleService, times(1)).add(request);

    TeacherScheduleResponseMessage response = captureResponse();
    assertEquals("req-002", response.getRequestId());
    assertEquals(Boolean.TRUE, ReflectionTestUtils.getField(response, "success"));
    assertEquals("新增成功", ReflectionTestUtils.getField(response, "message"));
    assertSame(data, ReflectionTestUtils.getField(response, "data"));
    assertNull(ReflectionTestUtils.getField(response, "list"));
  }

  @Test
  void consume_update_success() {
    TeacherScheduleRequestMessage message = mock(TeacherScheduleRequestMessage.class);
    TeacherScheduleSaveRequest request = mock(TeacherScheduleSaveRequest.class);
    TeacherScheduleVO data = mock(TeacherScheduleVO.class);

    when(message.getRequestId()).thenReturn("req-003");
    when(message.getAction()).thenReturn("UPDATE");
    when(message.getSaveRequest()).thenReturn(request);
    when(localTeacherScheduleService.update(request)).thenReturn(data);

    consumer.consume(message);

    verify(localTeacherScheduleService, times(1)).update(request);

    TeacherScheduleResponseMessage response = captureResponse();
    assertEquals("req-003", response.getRequestId());
    assertEquals(Boolean.TRUE, ReflectionTestUtils.getField(response, "success"));
    assertEquals("修改成功", ReflectionTestUtils.getField(response, "message"));
    assertSame(data, ReflectionTestUtils.getField(response, "data"));
    assertNull(ReflectionTestUtils.getField(response, "list"));
  }

  @Test
  void consume_delete_success() {
    TeacherScheduleRequestMessage message = mock(TeacherScheduleRequestMessage.class);
    TeacherScheduleDeleteRequest request = mock(TeacherScheduleDeleteRequest.class);

    when(message.getRequestId()).thenReturn("req-004");
    when(message.getAction()).thenReturn("DELETE");
    when(message.getDeleteRequest()).thenReturn(request);

    consumer.consume(message);

    verify(localTeacherScheduleService, times(1)).delete(request);

    TeacherScheduleResponseMessage response = captureResponse();
    assertEquals("req-004", response.getRequestId());
    assertEquals(Boolean.TRUE, ReflectionTestUtils.getField(response, "success"));
    assertEquals("停用成功", ReflectionTestUtils.getField(response, "message"));
    assertNull(ReflectionTestUtils.getField(response, "data"));
    assertNull(ReflectionTestUtils.getField(response, "list"));
  }

  @Test
  void consume_unsupportedAction_fail() {
    TeacherScheduleRequestMessage message = mock(TeacherScheduleRequestMessage.class);

    when(message.getRequestId()).thenReturn("req-005");
    when(message.getAction()).thenReturn("UNKNOWN");

    consumer.consume(message);

    verifyNoInteractions(localTeacherScheduleService);

    TeacherScheduleResponseMessage response = captureResponse();
    assertEquals("req-005", response.getRequestId());
    assertEquals(Boolean.FALSE, ReflectionTestUtils.getField(response, "success"));
    assertEquals("不支持的操作类型", ReflectionTestUtils.getField(response, "message"));
    assertNull(ReflectionTestUtils.getField(response, "data"));
    assertNull(ReflectionTestUtils.getField(response, "list"));
  }

  @Test
  void consume_exception_fail() {
    TeacherScheduleRequestMessage message = mock(TeacherScheduleRequestMessage.class);
    TeacherScheduleQueryRequest request = mock(TeacherScheduleQueryRequest.class);

    when(message.getRequestId()).thenReturn("req-006");
    when(message.getAction()).thenReturn("QUERY");
    when(message.getQueryRequest()).thenReturn(request);
    when(localTeacherScheduleService.query(request)).thenThrow(new RuntimeException("查询失败"));

    consumer.consume(message);

    verify(localTeacherScheduleService, times(1)).query(request);

    TeacherScheduleResponseMessage response = captureResponse();
    assertEquals("req-006", response.getRequestId());
    assertEquals(Boolean.FALSE, ReflectionTestUtils.getField(response, "success"));
    assertEquals("查询失败", ReflectionTestUtils.getField(response, "message"));
    assertNull(ReflectionTestUtils.getField(response, "data"));
    assertNull(ReflectionTestUtils.getField(response, "list"));
  }

  private TeacherScheduleResponseMessage captureResponse() {
    ArgumentCaptor<TeacherScheduleResponseMessage> captor =
        ArgumentCaptor.forClass(TeacherScheduleResponseMessage.class);
    verify(teacherScheduleResponseProducer, atLeastOnce()).send(captor.capture());
    return captor.getValue();
  }
}