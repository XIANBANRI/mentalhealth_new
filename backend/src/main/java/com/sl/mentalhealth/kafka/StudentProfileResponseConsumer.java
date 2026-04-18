package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.StudentProfileResponseMessage;
import com.sl.mentalhealth.service.PendingStudentProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StudentProfileResponseConsumer {

  private static final Logger log = LoggerFactory.getLogger(StudentProfileResponseConsumer.class);

  private final PendingStudentProfileService pendingStudentProfileService;

  public StudentProfileResponseConsumer(
      PendingStudentProfileService pendingStudentProfileService) {
    this.pendingStudentProfileService = pendingStudentProfileService;
  }

  @KafkaListener(
      topics = KafkaTopics.STUDENT_PROFILE_RESPONSE,
      groupId = "mh-student-profile-response-group",
      containerFactory = "studentProfileResponseKafkaListenerContainerFactory"
  )
  public void onMessage(StudentProfileResponseMessage message) {
    log.info("收到学生档案响应消息, requestId={}, success={}",
        message.getRequestId(), message.getSuccess());

    pendingStudentProfileService.complete(message.getRequestId(), message);
  }
}