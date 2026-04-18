package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminTeacherManageResponseMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminTeacherManageResponseProducer {

  private final KafkaTemplate<String, AdminTeacherManageResponseMessage> kafkaTemplate;

  public AdminTeacherManageResponseProducer(
      @Qualifier("adminTeacherManageResponseKafkaTemplate")
      KafkaTemplate<String, AdminTeacherManageResponseMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(AdminTeacherManageResponseMessage message) {
    kafkaTemplate.send(KafkaTopics.ADMIN_TEACHER_MANAGE_RESPONSE, message.getRequestId(), message);
  }
}