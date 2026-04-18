package com.sl.mentalhealth.service;

import com.sl.mentalhealth.dto.AppointmentCancelRequest;
import com.sl.mentalhealth.dto.AppointmentCreateRequest;
import com.sl.mentalhealth.dto.TeacherAppointmentAuditRequest;
import com.sl.mentalhealth.kafka.AppointmentRequestProducer;
import com.sl.mentalhealth.kafka.message.AppointmentRequestMessage;
import com.sl.mentalhealth.kafka.message.AppointmentResponseMessage;
import com.sl.mentalhealth.vo.AppointmentVO;
import com.sl.mentalhealth.vo.AvailableAppointmentVO;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentGatewayService {

  private final AppointmentRequestProducer appointmentRequestProducer;
  private final PendingAppointmentService pendingAppointmentService;

  public List<AvailableAppointmentVO> studentAvailable(String date) {
    AppointmentRequestMessage request = new AppointmentRequestMessage();
    request.setOperation("STUDENT_AVAILABLE");
    request.setRequestId(UUID.randomUUID().toString());
    request.setDate(date);

    AppointmentResponseMessage response = sendAndWait(request);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getAvailableTeachers();
  }

  public Long studentCreate(AppointmentCreateRequest req) {
    AppointmentRequestMessage request = new AppointmentRequestMessage();
    request.setOperation("STUDENT_CREATE");
    request.setRequestId(UUID.randomUUID().toString());
    request.setStudentId(req.getStudentId());
    request.setScheduleId(req.getScheduleId());
    request.setAppointmentDate(req.getAppointmentDate());
    request.setPurpose(req.getPurpose());
    request.setRemark(req.getRemark());

    AppointmentResponseMessage response = sendAndWait(request);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getAppointmentId();
  }

  public List<AppointmentVO> studentMy(String studentId) {
    AppointmentRequestMessage request = new AppointmentRequestMessage();
    request.setOperation("STUDENT_MY");
    request.setRequestId(UUID.randomUUID().toString());
    request.setStudentId(studentId);

    AppointmentResponseMessage response = sendAndWait(request);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getAppointmentList();
  }

  public void studentCancel(AppointmentCancelRequest req) {
    AppointmentRequestMessage request = new AppointmentRequestMessage();
    request.setOperation("STUDENT_CANCEL");
    request.setRequestId(UUID.randomUUID().toString());
    request.setStudentId(req.getStudentId());
    request.setAppointmentId(req.getAppointmentId());

    AppointmentResponseMessage response = sendAndWait(request);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
  }

  public List<AppointmentVO> teacherList(String teacherAccount, String status, String date) {
    AppointmentRequestMessage request = new AppointmentRequestMessage();
    request.setOperation("TEACHER_LIST");
    request.setRequestId(UUID.randomUUID().toString());
    request.setTeacherAccount(teacherAccount);
    request.setStatus(status);
    request.setDate(date);

    AppointmentResponseMessage response = sendAndWait(request);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
    return response.getAppointmentList();
  }

  public void teacherApprove(TeacherAppointmentAuditRequest req) {
    AppointmentRequestMessage request = new AppointmentRequestMessage();
    request.setOperation("TEACHER_APPROVE");
    request.setRequestId(UUID.randomUUID().toString());
    request.setAppointmentId(req.getAppointmentId());
    request.setTeacherAccount(req.getTeacherAccount());
    request.setTeacherReply(req.getTeacherReply());

    AppointmentResponseMessage response = sendAndWait(request);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
  }

  public void teacherReject(TeacherAppointmentAuditRequest req) {
    AppointmentRequestMessage request = new AppointmentRequestMessage();
    request.setOperation("TEACHER_REJECT");
    request.setRequestId(UUID.randomUUID().toString());
    request.setAppointmentId(req.getAppointmentId());
    request.setTeacherAccount(req.getTeacherAccount());
    request.setRejectReason(req.getTeacherReply());

    AppointmentResponseMessage response = sendAndWait(request);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
  }

  public void teacherComplete(TeacherAppointmentAuditRequest req) {
    AppointmentRequestMessage request = new AppointmentRequestMessage();
    request.setOperation("TEACHER_COMPLETE");
    request.setRequestId(UUID.randomUUID().toString());
    request.setAppointmentId(req.getAppointmentId());
    request.setTeacherAccount(req.getTeacherAccount());
    request.setTeacherReply(req.getTeacherReply());

    AppointmentResponseMessage response = sendAndWait(request);
    if (!response.isSuccess()) {
      throw new RuntimeException(response.getMessage());
    }
  }

  private AppointmentResponseMessage sendAndWait(AppointmentRequestMessage request) {
    try {
      CompletableFuture<AppointmentResponseMessage> future =
          pendingAppointmentService.createFuture(request.getRequestId());

      appointmentRequestProducer.send(request);

      return future.get(10, TimeUnit.SECONDS);
    } catch (Exception e) {
      pendingAppointmentService.remove(request.getRequestId());
      throw new RuntimeException("预约服务处理失败：" + e.getMessage(), e);
    }
  }
}