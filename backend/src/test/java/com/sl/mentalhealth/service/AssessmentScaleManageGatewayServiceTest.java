package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.dto.AssessmentScaleUpdateRequest;
import com.sl.mentalhealth.kafka.AssessmentScaleManageRequestProducer;
import com.sl.mentalhealth.kafka.message.AssessmentScaleManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AssessmentScaleManageResponseMessage;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssessmentScaleManageGatewayServiceTest {

  @Mock
  private AssessmentScaleManageRequestProducer assessmentScaleManageRequestProducer;

  @Mock
  private PendingAssessmentScaleManageService pendingAssessmentScaleManageService;

  @InjectMocks
  private AssessmentScaleManageGatewayService service;

  @Test
  void importScale_success() {
    AssessmentScaleManageRequestMessage message = new AssessmentScaleManageRequestMessage();
    message.setRequestId("req-1");
    message.setAction("IMPORT");

    AssessmentScaleManageResponseMessage response = new AssessmentScaleManageResponseMessage();
    response.setSuccess(true);
    CompletableFuture<AssessmentScaleManageResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAssessmentScaleManageService.create("req-1")).thenReturn(future);

    AssessmentScaleManageResponseMessage result = service.importScale(message);

    assertSame(response, result);
    verify(assessmentScaleManageRequestProducer).send(message);
    verify(pendingAssessmentScaleManageService).create("req-1");
  }

  @Test
  void listAll_success() {
    AssessmentScaleManageResponseMessage response = new AssessmentScaleManageResponseMessage();
    response.setSuccess(true);
    CompletableFuture<AssessmentScaleManageResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAssessmentScaleManageService.create(anyString())).thenReturn(future);

    AssessmentScaleManageResponseMessage result = service.listAll();

    assertSame(response, result);

    ArgumentCaptor<AssessmentScaleManageRequestMessage> captor =
        ArgumentCaptor.forClass(AssessmentScaleManageRequestMessage.class);
    verify(assessmentScaleManageRequestProducer).send(captor.capture());
    AssessmentScaleManageRequestMessage sent = captor.getValue();
    assertEquals("LIST", sent.getAction());
  }

  @Test
  void detail_success() {
    AssessmentScaleManageResponseMessage response = new AssessmentScaleManageResponseMessage();
    response.setSuccess(true);
    CompletableFuture<AssessmentScaleManageResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAssessmentScaleManageService.create(anyString())).thenReturn(future);

    AssessmentScaleManageResponseMessage result = service.detail(8L);

    assertSame(response, result);

    ArgumentCaptor<AssessmentScaleManageRequestMessage> captor =
        ArgumentCaptor.forClass(AssessmentScaleManageRequestMessage.class);
    verify(assessmentScaleManageRequestProducer).send(captor.capture());
    AssessmentScaleManageRequestMessage sent = captor.getValue();
    assertEquals("DETAIL", sent.getAction());
    assertEquals(8L, sent.getScaleId());
  }

  @Test
  void update_success() {
    AssessmentScaleUpdateRequest request = org.mockito.Mockito.mock(AssessmentScaleUpdateRequest.class);
    when(request.getScaleId()).thenReturn(5L);
    when(request.getScaleName()).thenReturn("SDS");
    when(request.getScaleType()).thenReturn("抑郁");
    when(request.getDescription()).thenReturn("描述");
    when(request.getOperator()).thenReturn("admin");
    when(request.getVersionRemark()).thenReturn("v2");
    when(request.getQuestions()).thenReturn(Collections.emptyList());
    when(request.getRules()).thenReturn(Collections.emptyList());

    AssessmentScaleManageResponseMessage response = new AssessmentScaleManageResponseMessage();
    response.setSuccess(true);
    CompletableFuture<AssessmentScaleManageResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAssessmentScaleManageService.create(anyString())).thenReturn(future);

    AssessmentScaleManageResponseMessage result = service.update(request);

    assertSame(response, result);

    ArgumentCaptor<AssessmentScaleManageRequestMessage> captor =
        ArgumentCaptor.forClass(AssessmentScaleManageRequestMessage.class);
    verify(assessmentScaleManageRequestProducer).send(captor.capture());
    AssessmentScaleManageRequestMessage sent = captor.getValue();
    assertEquals("UPDATE", sent.getAction());
    assertEquals(5L, sent.getScaleId());
    assertEquals("SDS", sent.getScaleName());
    assertEquals("抑郁", sent.getScaleType());
    assertEquals("描述", sent.getDescription());
    assertEquals("admin", sent.getOperator());
    assertEquals("v2", sent.getVersionRemark());
    assertEquals(Collections.emptyList(), sent.getQuestions());
    assertEquals(Collections.emptyList(), sent.getRules());
  }

  @Test
  void enable_disable_delete_success() {
    AssessmentScaleManageResponseMessage response = new AssessmentScaleManageResponseMessage();
    response.setSuccess(true);
    CompletableFuture<AssessmentScaleManageResponseMessage> future =
        CompletableFuture.completedFuture(response);
    when(pendingAssessmentScaleManageService.create(anyString())).thenReturn(future);

    service.enable(1L);
    service.disable(2L);
    service.delete(3L);

    ArgumentCaptor<AssessmentScaleManageRequestMessage> captor =
        ArgumentCaptor.forClass(AssessmentScaleManageRequestMessage.class);
    verify(assessmentScaleManageRequestProducer, org.mockito.Mockito.times(3)).send(captor.capture());
    assertEquals("ENABLE", captor.getAllValues().get(0).getAction());
    assertEquals(1L, captor.getAllValues().get(0).getScaleId());
    assertEquals("DISABLE", captor.getAllValues().get(1).getAction());
    assertEquals(2L, captor.getAllValues().get(1).getScaleId());
    assertEquals("DELETE", captor.getAllValues().get(2).getAction());
    assertEquals(3L, captor.getAllValues().get(2).getScaleId());
  }

  @Test
  void sendAndAwait_timeout_returnsFailResponseAndRemovesPending() {
    CompletableFuture<AssessmentScaleManageResponseMessage> future = new CompletableFuture<>();
    when(pendingAssessmentScaleManageService.create(anyString())).thenReturn(future);

    AssessmentScaleManageResponseMessage result = service.listAll();

    assertEquals(Boolean.FALSE, result.getSuccess());
    assertTrue(result.getMessage().startsWith("Kafka请求超时："));

    ArgumentCaptor<AssessmentScaleManageRequestMessage> captor =
        ArgumentCaptor.forClass(AssessmentScaleManageRequestMessage.class);
    verify(assessmentScaleManageRequestProducer).send(captor.capture());
    verify(pendingAssessmentScaleManageService).remove(captor.getValue().getRequestId());
  }
}
