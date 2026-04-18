package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.StudentProfileRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class StudentProfileRequestProducer {

  private static final Logger log = LoggerFactory.getLogger(StudentProfileRequestProducer.class);

  private final KafkaTemplate<String, StudentProfileRequestMessage> kafkaTemplate;

  public StudentProfileRequestProducer(
      KafkaTemplate<String, StudentProfileRequestMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(StudentProfileRequestMessage message) {
    log.info("Producer发送学生档案请求, action={}, requestId={}, studentId={}",
        message.getAction(), message.getRequestId(), message.getStudentId());

    kafkaTemplate.send(KafkaTopics.STUDENT_PROFILE_REQUEST, message.getRequestId(), message);
  }
}