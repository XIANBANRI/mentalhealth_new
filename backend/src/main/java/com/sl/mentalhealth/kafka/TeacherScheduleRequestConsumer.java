package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.dto.TeacherScheduleDeleteRequest;
import com.sl.mentalhealth.dto.TeacherScheduleQueryRequest;
import com.sl.mentalhealth.dto.TeacherScheduleSaveRequest;
import com.sl.mentalhealth.kafka.message.TeacherScheduleRequestMessage;
import com.sl.mentalhealth.kafka.message.TeacherScheduleResponseMessage;
import com.sl.mentalhealth.service.LocalTeacherScheduleService;
import com.sl.mentalhealth.vo.TeacherScheduleVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherScheduleRequestConsumer {

  private final LocalTeacherScheduleService localTeacherScheduleService;
  private final TeacherScheduleResponseProducer teacherScheduleResponseProducer;

  @KafkaListener(
      topics = KafkaTopics.TEACHER_SCHEDULE_REQUEST,
      groupId = "mental-health-teacher-schedule-request-group",
      containerFactory = "teacherScheduleRequestKafkaListenerContainerFactory"
  )
  public void consume(TeacherScheduleRequestMessage message) {
    TeacherScheduleResponseMessage response = new TeacherScheduleResponseMessage();
    response.setRequestId(message.getRequestId());

    try {
      String action = message.getAction();

      if ("QUERY".equals(action)) {
        TeacherScheduleQueryRequest request = message.getQueryRequest();
        List<TeacherScheduleVO> list = localTeacherScheduleService.query(request);
        response.setSuccess(true);
        response.setMessage("查询成功");
        response.setList(list);
      } else if ("ADD".equals(action)) {
        TeacherScheduleSaveRequest request = message.getSaveRequest();
        TeacherScheduleVO data = localTeacherScheduleService.add(request);
        response.setSuccess(true);
        response.setMessage("新增成功");
        response.setData(data);
      } else if ("UPDATE".equals(action)) {
        TeacherScheduleSaveRequest request = message.getSaveRequest();
        TeacherScheduleVO data = localTeacherScheduleService.update(request);
        response.setSuccess(true);
        response.setMessage("修改成功");
        response.setData(data);
      } else if ("DELETE".equals(action)) {
        TeacherScheduleDeleteRequest request = message.getDeleteRequest();
        localTeacherScheduleService.delete(request);
        response.setSuccess(true);
        response.setMessage("停用成功");
      } else {
        response.setSuccess(false);
        response.setMessage("不支持的操作类型");
      }
    } catch (Exception e) {
      response.setSuccess(false);
      response.setMessage(e.getMessage());
    }

    teacherScheduleResponseProducer.send(response);
  }
}