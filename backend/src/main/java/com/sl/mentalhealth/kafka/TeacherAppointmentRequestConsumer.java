package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.dto.TeacherAppointmentQueryRequest;
import com.sl.mentalhealth.dto.TeacherAppointmentUpdateStatusRequest;
import com.sl.mentalhealth.dto.TeacherAssessmentRecordQueryRequest;
import com.sl.mentalhealth.kafka.message.TeacherAppointmentRequestMessage;
import com.sl.mentalhealth.kafka.message.TeacherAppointmentResponseMessage;
import com.sl.mentalhealth.service.LocalTeacherAppointmentService;
import com.sl.mentalhealth.vo.TeacherAppointmentVO;
import com.sl.mentalhealth.vo.TeacherAssessmentRecordVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherAppointmentRequestConsumer {

  private final LocalTeacherAppointmentService localTeacherAppointmentService;
  private final TeacherAppointmentResponseProducer teacherAppointmentResponseProducer;

  @KafkaListener(
      topics = KafkaTopics.TEACHER_APPOINTMENT_REQUEST,
      groupId = "mental-health-teacher-appointment-request-group",
      containerFactory = "teacherAppointmentRequestKafkaListenerContainerFactory"
  )
  public void consume(TeacherAppointmentRequestMessage message) {
    TeacherAppointmentResponseMessage response = new TeacherAppointmentResponseMessage();
    response.setRequestId(message.getRequestId());

    try {
      String action = message.getAction();

      if ("QUERY".equals(action)) {
        TeacherAppointmentQueryRequest request = message.getQueryRequest();
        List<TeacherAppointmentVO> list = localTeacherAppointmentService.query(request);
        response.setSuccess(true);
        response.setMessage("查询成功");
        response.setAppointmentList(list);
      } else if ("RECORD".equals(action)) {
        TeacherAppointmentQueryRequest request = message.getQueryRequest();
        List<TeacherAppointmentVO> list = localTeacherAppointmentService.record(request);
        response.setSuccess(true);
        response.setMessage("查询成功");
        response.setAppointmentList(list);
      } else if ("UPDATE_STATUS".equals(action)) {
        TeacherAppointmentUpdateStatusRequest request = message.getUpdateStatusRequest();
        TeacherAppointmentVO data = localTeacherAppointmentService.updateStatus(request);
        response.setSuccess(true);
        response.setMessage("操作成功");
        response.setAppointmentData(data);
      } else if ("ASSESSMENT_RECORD".equals(action)) {
        TeacherAssessmentRecordQueryRequest request = message.getAssessmentRecordQueryRequest();
        List<TeacherAssessmentRecordVO> list = localTeacherAppointmentService.assessmentRecord(request);
        response.setSuccess(true);
        response.setMessage("查询成功");
        response.setAssessmentRecordList(list);
      } else {
        response.setSuccess(false);
        response.setMessage("不支持的操作类型");
      }
    } catch (Exception e) {
      response.setSuccess(false);
      response.setMessage(e.getMessage());
    }

    teacherAppointmentResponseProducer.send(response);
  }
}