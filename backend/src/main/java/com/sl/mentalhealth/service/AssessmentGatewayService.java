package com.sl.mentalhealth.service;

import com.sl.mentalhealth.dto.AssessmentSubmitRequest;
import com.sl.mentalhealth.kafka.AssessmentRequestProducer;
import com.sl.mentalhealth.kafka.message.AssessmentRequestMessage;
import com.sl.mentalhealth.kafka.message.AssessmentResponseMessage;
import com.sl.mentalhealth.vo.AssessmentRecordVO;
import com.sl.mentalhealth.vo.AssessmentScaleDetailVO;
import com.sl.mentalhealth.vo.AssessmentScaleVO;
import com.sl.mentalhealth.vo.AssessmentSubmitResultVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class AssessmentGatewayService {

  private final AssessmentRequestProducer assessmentRequestProducer;
  private final PendingAssessmentService pendingAssessmentService;

  public AssessmentGatewayService(AssessmentRequestProducer assessmentRequestProducer,
      PendingAssessmentService pendingAssessmentService) {
    this.assessmentRequestProducer = assessmentRequestProducer;
    this.pendingAssessmentService = pendingAssessmentService;
  }

  public List<AssessmentScaleVO> listScales() {
    AssessmentResponseMessage response = call(buildRequest(LocalAssessmentService.ACTION_LIST_SCALES));
    return response.getScales();
  }

  public AssessmentScaleDetailVO getDetail(Long scaleId) {
    AssessmentRequestMessage request = buildRequest(LocalAssessmentService.ACTION_GET_DETAIL);
    request.setScaleId(scaleId);
    AssessmentResponseMessage response = call(request);
    return response.getDetail();
  }

  public AssessmentSubmitResultVO submit(AssessmentSubmitRequest submitRequest) {
    AssessmentRequestMessage request = buildRequest(LocalAssessmentService.ACTION_SUBMIT);
    request.setStudentId(submitRequest.getStudentId());
    request.setSemester(submitRequest.getSemester());
    request.setScaleId(submitRequest.getScaleId());
    request.setVersionId(submitRequest.getVersionId());
    request.setAnswers(submitRequest.getAnswers());

    AssessmentResponseMessage response = call(request);
    return response.getSubmitResult();
  }

  public List<AssessmentRecordVO> getRecords(String studentId) {
    AssessmentRequestMessage request = buildRequest(LocalAssessmentService.ACTION_GET_RECORDS);
    request.setStudentId(studentId);
    AssessmentResponseMessage response = call(request);
    return response.getRecords();
  }

  private AssessmentRequestMessage buildRequest(String action) {
    AssessmentRequestMessage request = new AssessmentRequestMessage();
    request.setRequestId(UUID.randomUUID().toString());
    request.setAction(action);
    return request;
  }

  private AssessmentResponseMessage call(AssessmentRequestMessage request) {
    CompletableFuture<AssessmentResponseMessage> future =
        pendingAssessmentService.create(request.getRequestId());

    try {
      assessmentRequestProducer.send(request);
      AssessmentResponseMessage response = future.get(15, TimeUnit.SECONDS);

      if (response == null) {
        throw new RuntimeException("心理测评服务无响应");
      }

      if (!Boolean.TRUE.equals(response.getSuccess())) {
        throw new RuntimeException(response.getMessage());
      }

      return response;
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage() == null ? "心理测评服务异常" : e.getMessage());
    } finally {
      pendingAssessmentService.remove(request.getRequestId());
    }
  }
}