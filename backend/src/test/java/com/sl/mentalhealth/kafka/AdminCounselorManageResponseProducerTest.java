package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminCounselorManageResponseMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCounselorManageResponseProducerTest {

  @Mock
  private KafkaTemplate<String, AdminCounselorManageResponseMessage> kafkaTemplate;

  @InjectMocks
  private AdminCounselorManageResponseProducer producer;

  @Test
  void send_success() {
    AdminCounselorManageResponseMessage message = mock(AdminCounselorManageResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.ADMIN_COUNSELOR_MANAGE_RESPONSE,
        "req-001",
        message
    );
  }
}