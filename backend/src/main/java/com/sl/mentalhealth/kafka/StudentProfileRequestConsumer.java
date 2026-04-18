package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.StudentProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.StudentProfileResponseMessage;
import com.sl.mentalhealth.service.LocalStudentProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StudentProfileRequestConsumer {

  private static final Logger log = LoggerFactory.getLogger(StudentProfileRequestConsumer.class);

  private final LocalStudentProfileService localStudentProfileService;
  private final StudentProfileResponseProducer studentProfileResponseProducer;

  public StudentProfileRequestConsumer(LocalStudentProfileService localStudentProfileService,
      StudentProfileResponseProducer studentProfileResponseProducer) {
    this.localStudentProfileService = localStudentProfileService;
    this.studentProfileResponseProducer = studentProfileResponseProducer;
  }

  @KafkaListener(
      topics = KafkaTopics.STUDENT_PROFILE_REQUEST,
      groupId = "mh-student-profile-request-group",
      containerFactory = "studentProfileRequestKafkaListenerContainerFactory"
  )
  public void onMessage(StudentProfileRequestMessage message) {
    log.info("Consumer收到学生档案请求, action={}, requestId={}, studentId={}",
        message.getAction(), message.getRequestId(), message.getStudentId());

    StudentProfileResponseMessage response = localStudentProfileService.handle(message);
    studentProfileResponseProducer.send(response);

    log.info("Consumer发送学生档案响应, requestId={}, success={}",
        response.getRequestId(), response.getSuccess());
  }
}