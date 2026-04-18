package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminCounselorManageRequestMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCounselorManageRequestProducerTest {

  @Mock
  private KafkaTemplate<String, AdminCounselorManageRequestMessage> kafkaTemplate;

  @InjectMocks
  private AdminCounselorManageRequestProducer producer;

  @Test
  void send_success() {
    AdminCounselorManageRequestMessage message = mock(AdminCounselorManageRequestMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.ADMIN_COUNSELOR_MANAGE_REQUEST,
        "req-001",
        message
    );
  }
}