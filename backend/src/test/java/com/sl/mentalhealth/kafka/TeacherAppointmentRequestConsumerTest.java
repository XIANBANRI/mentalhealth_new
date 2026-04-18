package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.dto.TeacherAppointmentQueryRequest;
import com.sl.mentalhealth.dto.TeacherAppointmentUpdateStatusRequest;
import com.sl.mentalhealth.dto.TeacherAssessmentRecordQueryRequest;
import com.sl.mentalhealth.kafka.message.TeacherAppointmentRequestMessage;
import com.sl.mentalhealth.kafka.message.TeacherAppointmentResponseMessage;
import com.sl.mentalhealth.service.LocalTeacherAppointmentService;
import com.sl.mentalhealth.vo.TeacherAppointmentVO;
import com.sl.mentalhealth.vo.TeacherAssessmentRecordVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherAppointmentRequestConsumerTest {

  @Mock
  private LocalTeacherAppointmentService localTeacherAppointmentService;

  @Mock
  private TeacherAppointmentResponseProducer teacherAppointmentResponseProducer;

  @InjectMocks
  private TeacherAppointmentRequestConsumer consumer;

  @Test
  void consume_query_success() {
    TeacherAppointmentRequestMessage message = mock(TeacherAppointmentRequestMessage.class);
    TeacherAppointmentQueryRequest queryRequest = mock(TeacherAppointmentQueryRequest.class);
    List<TeacherAppointmentVO> list = Collections.singletonList(mock(TeacherAppointmentVO.class));

    when(message.getRequestId()).thenReturn("req-001");
    when(message.getAction()).thenReturn("QUERY");
    when(message.getQueryRequest()).thenReturn(queryRequest);
    when(localTeacherAppointmentService.query(queryRequest)).thenReturn(list);

    consumer.consume(message);

    verify(localTeacherAppointmentService, times(1)).query(queryRequest);

    TeacherAppointmentResponseMessage response = captureResponse();
    assertEquals("req-001", response.getRequestId());
    assertTrue(response.isSuccess());
    assertEquals("查询成功", response.getMessage());
    assertEquals(list, response.getAppointmentList());
    assertNull(response.getAppointmentData());
    assertNull(response.getAssessmentRecordList());
  }

  @Test
  void consume_record_success() {
    TeacherAppointmentRequestMessage message = mock(TeacherAppointmentRequestMessage.class);
    TeacherAppointmentQueryRequest queryRequest = mock(TeacherAppointmentQueryRequest.class);
    List<TeacherAppointmentVO> list = Collections.singletonList(mock(TeacherAppointmentVO.class));

    when(message.getRequestId()).thenReturn("req-002");
    when(message.getAction()).thenReturn("RECORD");
    when(message.getQueryRequest()).thenReturn(queryRequest);
    when(localTeacherAppointmentService.record(queryRequest)).thenReturn(list);

    consumer.consume(message);

    verify(localTeacherAppointmentService, times(1)).record(queryRequest);

    TeacherAppointmentResponseMessage response = captureResponse();
    assertEquals("req-002", response.getRequestId());
    assertTrue(response.isSuccess());
    assertEquals("查询成功", response.getMessage());
    assertEquals(list, response.getAppointmentList());
    assertNull(response.getAppointmentData());
    assertNull(response.getAssessmentRecordList());
  }

  @Test
  void consume_updateStatus_success() {
    TeacherAppointmentRequestMessage message = mock(TeacherAppointmentRequestMessage.class);
    TeacherAppointmentUpdateStatusRequest updateRequest = mock(TeacherAppointmentUpdateStatusRequest.class);
    TeacherAppointmentVO data = mock(TeacherAppointmentVO.class);

    when(message.getRequestId()).thenReturn("req-003");
    when(message.getAction()).thenReturn("UPDATE_STATUS");
    when(message.getUpdateStatusRequest()).thenReturn(updateRequest);
    when(localTeacherAppointmentService.updateStatus(updateRequest)).thenReturn(data);

    consumer.consume(message);

    verify(localTeacherAppointmentService, times(1)).updateStatus(updateRequest);

    TeacherAppointmentResponseMessage response = captureResponse();
    assertEquals("req-003", response.getRequestId());
    assertTrue(response.isSuccess());
    assertEquals("操作成功", response.getMessage());
    assertSame(data, response.getAppointmentData());
    assertNull(response.getAppointmentList());
    assertNull(response.getAssessmentRecordList());
  }

  @Test
  void consume_assessmentRecord_success() {
    TeacherAppointmentRequestMessage message = mock(TeacherAppointmentRequestMessage.class);
    TeacherAssessmentRecordQueryRequest recordQueryRequest = mock(TeacherAssessmentRecordQueryRequest.class);
    List<TeacherAssessmentRecordVO> list =
        Collections.singletonList(mock(TeacherAssessmentRecordVO.class));

    when(message.getRequestId()).thenReturn("req-004");
    when(message.getAction()).thenReturn("ASSESSMENT_RECORD");
    when(message.getAssessmentRecordQueryRequest()).thenReturn(recordQueryRequest);
    when(localTeacherAppointmentService.assessmentRecord(recordQueryRequest)).thenReturn(list);

    consumer.consume(message);

    verify(localTeacherAppointmentService, times(1)).assessmentRecord(recordQueryRequest);

    TeacherAppointmentResponseMessage response = captureResponse();
    assertEquals("req-004", response.getRequestId());
    assertTrue(response.isSuccess());
    assertEquals("查询成功", response.getMessage());
    assertEquals(list, response.getAssessmentRecordList());
    assertNull(response.getAppointmentList());
    assertNull(response.getAppointmentData());
  }

  @Test
  void consume_unsupportedAction_fail() {
    TeacherAppointmentRequestMessage message = mock(TeacherAppointmentRequestMessage.class);

    when(message.getRequestId()).thenReturn("req-005");
    when(message.getAction()).thenReturn("UNKNOWN");

    consumer.consume(message);

    verifyNoInteractions(localTeacherAppointmentService);

    TeacherAppointmentResponseMessage response = captureResponse();
    assertEquals("req-005", response.getRequestId());
    assertFalse(response.isSuccess());
    assertEquals("不支持的操作类型", response.getMessage());
    assertNull(response.getAppointmentList());
    assertNull(response.getAppointmentData());
    assertNull(response.getAssessmentRecordList());
  }

  @Test
  void consume_exception_fail() {
    TeacherAppointmentRequestMessage message = mock(TeacherAppointmentRequestMessage.class);
    TeacherAppointmentQueryRequest queryRequest = mock(TeacherAppointmentQueryRequest.class);

    when(message.getRequestId()).thenReturn("req-006");
    when(message.getAction()).thenReturn("QUERY");
    when(message.getQueryRequest()).thenReturn(queryRequest);
    when(localTeacherAppointmentService.query(queryRequest))
        .thenThrow(new RuntimeException("查询失败"));

    consumer.consume(message);

    verify(localTeacherAppointmentService, times(1)).query(queryRequest);

    TeacherAppointmentResponseMessage response = captureResponse();
    assertEquals("req-006", response.getRequestId());
    assertFalse(response.isSuccess());
    assertEquals("查询失败", response.getMessage());
    assertNull(response.getAppointmentList());
    assertNull(response.getAppointmentData());
    assertNull(response.getAssessmentRecordList());
  }

  private TeacherAppointmentResponseMessage captureResponse() {
    ArgumentCaptor<TeacherAppointmentResponseMessage> captor =
        ArgumentCaptor.forClass(TeacherAppointmentResponseMessage.class);
    verify(teacherAppointmentResponseProducer, atLeastOnce()).send(captor.capture());
    return captor.getValue();
  }
}