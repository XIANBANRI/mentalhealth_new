package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminTeacherManageRequestMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminTeacherManageRequestProducerTest {

  @Mock
  private KafkaTemplate<String, AdminTeacherManageRequestMessage> kafkaTemplate;

  @InjectMocks
  private AdminTeacherManageRequestProducer producer;

  @Test
  void send_success() {
    AdminTeacherManageRequestMessage message = mock(AdminTeacherManageRequestMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.ADMIN_TEACHER_MANAGE_REQUEST,
        "req-001",
        message
    );
  }
}