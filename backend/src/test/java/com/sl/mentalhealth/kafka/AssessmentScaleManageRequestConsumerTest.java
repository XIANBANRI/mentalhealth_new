package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentScaleManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AssessmentScaleManageResponseMessage;
import com.sl.mentalhealth.service.LocalAssessmentScaleManageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentScaleManageRequestConsumerTest {

  @Mock
  private LocalAssessmentScaleManageService localAssessmentScaleManageService;

  @Mock
  private AssessmentScaleManageResponseProducer assessmentScaleManageResponseProducer;

  @InjectMocks
  private AssessmentScaleManageRequestConsumer consumer;

  @Test
  void consume_import_success() {
    AssessmentScaleManageRequestMessage message = mock(AssessmentScaleManageRequestMessage.class);
    when(message.getAction()).thenReturn("IMPORT");
    when(message.getRequestId()).thenReturn("req-001");

    doReturn(null).when(localAssessmentScaleManageService).importScale(message);

    consumer.consume(message);

    verify(localAssessmentScaleManageService, times(1)).importScale(message);
    verify(localAssessmentScaleManageService, never()).listAll();
    verify(localAssessmentScaleManageService, never()).getScaleDetail(any());
    verify(localAssessmentScaleManageService, never()).updateScale(any());
    verify(localAssessmentScaleManageService, never()).enableScale(any());
    verify(localAssessmentScaleManageService, never()).disableScale(any());
    verify(localAssessmentScaleManageService, never()).deleteScale(any());

    AssessmentScaleManageResponseMessage response = captureResponse();
    assertEquals("req-001", response.getRequestId());
  }

  @Test
  void consume_list_success() {
    AssessmentScaleManageRequestMessage message = mock(AssessmentScaleManageRequestMessage.class);
    when(message.getAction()).thenReturn("LIST");
    when(message.getRequestId()).thenReturn("req-002");

    doReturn(Collections.emptyList()).when(localAssessmentScaleManageService).listAll();

    consumer.consume(message);

    verify(localAssessmentScaleManageService, times(1)).listAll();
    verify(localAssessmentScaleManageService, never()).importScale(any());

    AssessmentScaleManageResponseMessage response = captureResponse();
    assertEquals("req-002", response.getRequestId());
  }

  @Test
  void consume_detail_success() {
    AssessmentScaleManageRequestMessage message = mock(AssessmentScaleManageRequestMessage.class);
    when(message.getAction()).thenReturn("DETAIL");
    when(message.getRequestId()).thenReturn("req-003");
    when(message.getScaleId()).thenReturn(1L);

    doReturn(null).when(localAssessmentScaleManageService).getScaleDetail(1L);

    consumer.consume(message);

    verify(localAssessmentScaleManageService, times(1)).getScaleDetail(1L);

    AssessmentScaleManageResponseMessage response = captureResponse();
    assertEquals("req-003", response.getRequestId());
  }

  @Test
  void consume_update_success() {
    AssessmentScaleManageRequestMessage message = mock(AssessmentScaleManageRequestMessage.class);
    when(message.getAction()).thenReturn("UPDATE");
    when(message.getRequestId()).thenReturn("req-004");

    doReturn(null).when(localAssessmentScaleManageService).updateScale(message);

    consumer.consume(message);

    verify(localAssessmentScaleManageService, times(1)).updateScale(message);

    AssessmentScaleManageResponseMessage response = captureResponse();
    assertEquals("req-004", response.getRequestId());
  }

  @Test
  void consume_enable_success() {
    AssessmentScaleManageRequestMessage message = mock(AssessmentScaleManageRequestMessage.class);
    when(message.getAction()).thenReturn("ENABLE");
    when(message.getRequestId()).thenReturn("req-005");
    when(message.getScaleId()).thenReturn(2L);

    doReturn(null).when(localAssessmentScaleManageService).enableScale(2L);

    consumer.consume(message);

    verify(localAssessmentScaleManageService, times(1)).enableScale(2L);

    AssessmentScaleManageResponseMessage response = captureResponse();
    assertEquals("req-005", response.getRequestId());
  }

  @Test
  void consume_disable_success() {
    AssessmentScaleManageRequestMessage message = mock(AssessmentScaleManageRequestMessage.class);
    when(message.getAction()).thenReturn("DISABLE");
    when(message.getRequestId()).thenReturn("req-006");
    when(message.getScaleId()).thenReturn(3L);

    doReturn(null).when(localAssessmentScaleManageService).disableScale(3L);

    consumer.consume(message);

    verify(localAssessmentScaleManageService, times(1)).disableScale(3L);

    AssessmentScaleManageResponseMessage response = captureResponse();
    assertEquals("req-006", response.getRequestId());
  }

  @Test
  void consume_delete_success() {
    AssessmentScaleManageRequestMessage message = mock(AssessmentScaleManageRequestMessage.class);
    when(message.getAction()).thenReturn("DELETE");
    when(message.getRequestId()).thenReturn("req-007");
    when(message.getScaleId()).thenReturn(4L);

    doReturn(null).when(localAssessmentScaleManageService).deleteScale(4L);

    consumer.consume(message);

    verify(localAssessmentScaleManageService, times(1)).deleteScale(4L);

    AssessmentScaleManageResponseMessage response = captureResponse();
    assertEquals("req-007", response.getRequestId());
  }

  @Test
  void consume_unsupportedAction_sendFailResponse() {
    AssessmentScaleManageRequestMessage message = mock(AssessmentScaleManageRequestMessage.class);
    when(message.getAction()).thenReturn("UNKNOWN");
    when(message.getRequestId()).thenReturn("req-008");

    consumer.consume(message);

    verifyNoInteractions(localAssessmentScaleManageService);

    AssessmentScaleManageResponseMessage response = captureResponse();
    assertEquals("req-008", response.getRequestId());
  }

  @Test
  void consume_exception_sendFailResponse() {
    AssessmentScaleManageRequestMessage message = mock(AssessmentScaleManageRequestMessage.class);
    when(message.getAction()).thenReturn("LIST");
    when(message.getRequestId()).thenReturn("req-009");

    when(localAssessmentScaleManageService.listAll())
        .thenThrow(new RuntimeException("查询失败"));

    consumer.consume(message);

    verify(localAssessmentScaleManageService, times(1)).listAll();

    AssessmentScaleManageResponseMessage response = captureResponse();
    assertEquals("req-009", response.getRequestId());
  }

  private AssessmentScaleManageResponseMessage captureResponse() {
    ArgumentCaptor<AssessmentScaleManageResponseMessage> captor =
        ArgumentCaptor.forClass(AssessmentScaleManageResponseMessage.class);
    verify(assessmentScaleManageResponseProducer, atLeastOnce()).send(captor.capture());
    return captor.getValue();
  }
}