package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminProfileRequestMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminProfileRequestProducerTest {

  @Mock
  private KafkaTemplate<String, AdminProfileRequestMessage> kafkaTemplate;

  @InjectMocks
  private AdminProfileRequestProducer producer;

  @Test
  void send_success() {
    AdminProfileRequestMessage message = mock(AdminProfileRequestMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.ADMIN_PROFILE_REQUEST,
        "req-001",
        message
    );
  }

  @Test
  void send_nullMessage_doNothing() {
    producer.send(null);

    verifyNoInteractions(kafkaTemplate);
  }
}