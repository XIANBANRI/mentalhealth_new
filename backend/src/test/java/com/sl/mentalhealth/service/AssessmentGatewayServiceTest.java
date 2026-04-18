package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.dto.AssessmentSubmitRequest;
import com.sl.mentalhealth.kafka.AssessmentRequestProducer;
import com.sl.mentalhealth.kafka.message.AssessmentRequestMessage;
import com.sl.mentalhealth.kafka.message.AssessmentResponseMessage;
import com.sl.mentalhealth.vo.AssessmentRecordVO;
import com.sl.mentalhealth.vo.AssessmentScaleDetailVO;
import com.sl.mentalhealth.vo.AssessmentScaleVO;
import com.sl.mentalhealth.vo.AssessmentSubmitResultVO;
import java.util.Arrays;
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
class AssessmentGatewayServiceTest {

  @Mock
  private AssessmentRequestProducer assessmentRequestProducer;

  @Mock
  private PendingAssessmentService pendingAssessmentService;

  @InjectMocks
  private AssessmentGatewayService service;

  @Test
  void listScales_success() {
    List<AssessmentScaleVO> scales = Collections.singletonList(org.mockito.Mockito.mock(AssessmentScaleVO.class));
    AssessmentResponseMessage response = new AssessmentResponseMessage();
    response.setSuccess(true);
    response.setScales(scales);
    CompletableFuture<AssessmentResponseMessage> future = CompletableFuture.completedFuture(response);
    when(pendingAssessmentService.create(anyString())).thenReturn(future);

    List<AssessmentScaleVO> result = service.listScales();

    assertSame(scales, result);

    ArgumentCaptor<AssessmentRequestMessage> captor =
        ArgumentCaptor.forClass(AssessmentRequestMessage.class);
    verify(assessmentRequestProducer).send(captor.capture());
    AssessmentRequestMessage sent = captor.getValue();
    assertEquals(LocalAssessmentService.ACTION_LIST_SCALES, sent.getAction());
    verify(pendingAssessmentService).create(sent.getRequestId());
    verify(pendingAssessmentService).remove(sent.getRequestId());
  }

  @Test
  void getDetail_success() {
    AssessmentScaleDetailVO detail = org.mockito.Mockito.mock(AssessmentScaleDetailVO.class);
    AssessmentResponseMessage response = new AssessmentResponseMessage();
    response.setSuccess(true);
    response.setDetail(detail);
    CompletableFuture<AssessmentResponseMessage> future = CompletableFuture.completedFuture(response);
    when(pendingAssessmentService.create(anyString())).thenReturn(future);

    AssessmentScaleDetailVO result = service.getDetail(10L);

    assertSame(detail, result);

    ArgumentCaptor<AssessmentRequestMessage> captor =
        ArgumentCaptor.forClass(AssessmentRequestMessage.class);
    verify(assessmentRequestProducer).send(captor.capture());
    AssessmentRequestMessage sent = captor.getValue();
    assertEquals(LocalAssessmentService.ACTION_GET_DETAIL, sent.getAction());
    assertEquals(10L, sent.getScaleId());
  }

  @Test
  void submit_success() {
    AssessmentSubmitRequest request = org.mockito.Mockito.mock(AssessmentSubmitRequest.class);
    when(request.getStudentId()).thenReturn("s001");
    when(request.getSemester()).thenReturn("2025-2026-2");
    when(request.getScaleId()).thenReturn(1L);
    when(request.getVersionId()).thenReturn(2L);
    java.util.List answers = Arrays.asList(1, 2, 3);
    when(request.getAnswers()).thenReturn(answers);

    AssessmentSubmitResultVO submitResult = org.mockito.Mockito.mock(AssessmentSubmitResultVO.class);
    AssessmentResponseMessage response = new AssessmentResponseMessage();
    response.setSuccess(true);
    response.setSubmitResult(submitResult);
    CompletableFuture<AssessmentResponseMessage> future = CompletableFuture.completedFuture(response);
    when(pendingAssessmentService.create(anyString())).thenReturn(future);

    AssessmentSubmitResultVO result = service.submit(request);

    assertSame(submitResult, result);

    ArgumentCaptor<AssessmentRequestMessage> captor =
        ArgumentCaptor.forClass(AssessmentRequestMessage.class);
    verify(assessmentRequestProducer).send(captor.capture());
    AssessmentRequestMessage sent = captor.getValue();
    assertEquals(LocalAssessmentService.ACTION_SUBMIT, sent.getAction());
    assertEquals("s001", sent.getStudentId());
    assertEquals("2025-2026-2", sent.getSemester());
    assertEquals(1L, sent.getScaleId());
    assertEquals(2L, sent.getVersionId());
    assertEquals(answers, sent.getAnswers());
  }

  @Test
  void getRecords_success() {
    List<AssessmentRecordVO> records = Collections.singletonList(org.mockito.Mockito.mock(AssessmentRecordVO.class));
    AssessmentResponseMessage response = new AssessmentResponseMessage();
    response.setSuccess(true);
    response.setRecords(records);
    CompletableFuture<AssessmentResponseMessage> future = CompletableFuture.completedFuture(response);
    when(pendingAssessmentService.create(anyString())).thenReturn(future);

    List<AssessmentRecordVO> result = service.getRecords("s001");

    assertSame(records, result);

    ArgumentCaptor<AssessmentRequestMessage> captor =
        ArgumentCaptor.forClass(AssessmentRequestMessage.class);
    verify(assessmentRequestProducer).send(captor.capture());
    AssessmentRequestMessage sent = captor.getValue();
    assertEquals(LocalAssessmentService.ACTION_GET_RECORDS, sent.getAction());
    assertEquals("s001", sent.getStudentId());
  }

  @Test
  void call_responseNull_throwsException() {
    CompletableFuture<AssessmentResponseMessage> future = CompletableFuture.completedFuture(null);
    when(pendingAssessmentService.create(anyString())).thenReturn(future);

    RuntimeException ex = assertThrows(RuntimeException.class, () -> service.listScales());

    assertEquals("心理测评服务无响应", ex.getMessage());
    verify(pendingAssessmentService).remove(anyString());
  }

  @Test
  void call_responseFail_throwsException() {
    AssessmentResponseMessage response = new AssessmentResponseMessage();
    response.setSuccess(false);
    response.setMessage("提交失败");
    CompletableFuture<AssessmentResponseMessage> future = CompletableFuture.completedFuture(response);
    when(pendingAssessmentService.create(anyString())).thenReturn(future);

    RuntimeException ex = assertThrows(RuntimeException.class, () -> service.listScales());

    assertEquals("提交失败", ex.getMessage());
    verify(pendingAssessmentService).remove(anyString());
  }
}
