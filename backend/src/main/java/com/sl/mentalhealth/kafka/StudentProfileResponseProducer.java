package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.StudentProfileResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class StudentProfileResponseProducer {

  private static final Logger log = LoggerFactory.getLogger(StudentProfileResponseProducer.class);

  private final KafkaTemplate<String, StudentProfileResponseMessage> kafkaTemplate;

  public StudentProfileResponseProducer(
      KafkaTemplate<String, StudentProfileResponseMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(StudentProfileResponseMessage message) {
    log.info("Producer发送学生档案响应, requestId={}, success={}",
        message.getRequestId(), message.getSuccess());

    kafkaTemplate.send(KafkaTopics.STUDENT_PROFILE_RESPONSE, message.getRequestId(), message);
  }
}