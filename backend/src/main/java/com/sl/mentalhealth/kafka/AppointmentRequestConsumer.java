package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AppointmentRequestMessage;
import com.sl.mentalhealth.kafka.message.AppointmentResponseMessage;
import com.sl.mentalhealth.service.LocalAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentRequestConsumer {

  private final LocalAppointmentService localAppointmentService;
  private final AppointmentResponseProducer appointmentResponseProducer;

  @KafkaListener(
      topics = KafkaTopics.APPOINTMENT_REQUEST,
      groupId = "appointment-request-group",
      containerFactory = "appointmentRequestKafkaListenerContainerFactory"
  )
  public void consume(AppointmentRequestMessage request) {
    AppointmentResponseMessage response = new AppointmentResponseMessage();
    response.setRequestId(request.getRequestId());
    response.setOperation(request.getOperation());

    try {
      switch (request.getOperation()) {
        case "STUDENT_AVAILABLE" -> {
          response.setAvailableTeachers(localAppointmentService.studentAvailable(request.getDate()));
          response.setSuccess(true);
          response.setMessage("查询成功");
        }
        case "STUDENT_CREATE" -> {
          Long appointmentId = localAppointmentService.studentCreate(
              request.getStudentId(),
              request.getScheduleId(),
              request.getAppointmentDate(),
              request.getPurpose(),
              request.getRemark()
          );
          response.setAppointmentId(appointmentId);
          response.setSuccess(true);
          response.setMessage("预约提交成功");
        }
        case "STUDENT_MY" -> {
          response.setAppointmentList(localAppointmentService.studentMy(request.getStudentId()));
          response.setSuccess(true);
          response.setMessage("查询成功");
        }
        case "STUDENT_CANCEL" -> {
          localAppointmentService.studentCancel(request.getAppointmentId(), request.getStudentId());
          response.setSuccess(true);
          response.setMessage("取消成功");
        }
        case "TEACHER_LIST" -> {
          response.setAppointmentList(localAppointmentService.teacherList(
              request.getTeacherAccount(),
              request.getStatus(),
              request.getDate()
          ));
          response.setSuccess(true);
          response.setMessage("查询成功");
        }
        case "TEACHER_APPROVE" -> {
          localAppointmentService.teacherApprove(
              request.getAppointmentId(),
              request.getTeacherAccount(),
              request.getTeacherReply()
          );
          response.setSuccess(true);
          response.setMessage("通过成功");
        }
        case "TEACHER_REJECT" -> {
          localAppointmentService.teacherReject(
              request.getAppointmentId(),
              request.getTeacherAccount(),
              request.getRejectReason()
          );
          response.setSuccess(true);
          response.setMessage("拒绝成功");
        }
        case "TEACHER_COMPLETE" -> {
          localAppointmentService.teacherComplete(
              request.getAppointmentId(),
              request.getTeacherAccount(),
              request.getTeacherReply()
          );
          response.setSuccess(true);
          response.setMessage("完成成功");
        }
        default -> {
          response.setSuccess(false);
          response.setMessage("未知操作：" + request.getOperation());
        }
      }
    } catch (Exception e) {
      response.setSuccess(false);
      response.setMessage(e.getMessage());
    }

    appointmentResponseProducer.send(response);
  }
}