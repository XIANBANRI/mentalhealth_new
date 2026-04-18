package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.TeacherProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.TeacherProfileResponseMessage;
import com.sl.mentalhealth.service.LocalTeacherProfileService;
import com.sl.mentalhealth.vo.TeacherProfileResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherProfileRequestConsumer {

  private final LocalTeacherProfileService localTeacherProfileService;
  private final TeacherProfileResponseProducer teacherProfileResponseProducer;

  @KafkaListener(
      topics = KafkaTopics.TEACHER_PROFILE_REQUEST,
      groupId = "mental-health-teacher-profile-request-group",
      containerFactory = "teacherProfileRequestKafkaListenerContainerFactory"
  )
  public void consume(TeacherProfileRequestMessage message) {
    TeacherProfileResponseMessage response = new TeacherProfileResponseMessage();
    response.setRequestId(message.getRequestId());

    try {
      TeacherProfileResponseVO data;

      if (TeacherProfileRequestMessage.ACTION_UPDATE_AVATAR.equals(message.getAction())) {
        data = localTeacherProfileService.updateAvatar(
            message.getTeacherAccount(), message.getAvatarUrl());
        response.setSuccess(true);
        response.setMessage("头像上传成功");
      } else {
        data = localTeacherProfileService.getTeacherProfile(message.getTeacherAccount());
        response.setSuccess(true);
        response.setMessage("查询成功");
      }

      response.setData(data);
    } catch (Exception e) {
      response.setSuccess(false);
      response.setMessage(e.getMessage());
      response.setData(null);
    }

    teacherProfileResponseProducer.send(response);
  }
}