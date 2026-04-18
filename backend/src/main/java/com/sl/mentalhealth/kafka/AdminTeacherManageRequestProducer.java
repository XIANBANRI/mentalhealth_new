package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminTeacherManageRequestMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminTeacherManageRequestProducer {

  private final KafkaTemplate<String, AdminTeacherManageRequestMessage> kafkaTemplate;

  public AdminTeacherManageRequestProducer(
      @Qualifier("adminTeacherManageRequestKafkaTemplate")
      KafkaTemplate<String, AdminTeacherManageRequestMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(AdminTeacherManageRequestMessage message) {
    kafkaTemplate.send(KafkaTopics.ADMIN_TEACHER_MANAGE_REQUEST, message.getRequestId(), message);
  }
}