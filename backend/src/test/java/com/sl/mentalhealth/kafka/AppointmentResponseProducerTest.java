package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AppointmentResponseMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentResponseProducerTest {

  @Mock
  private KafkaTemplate<String, AppointmentResponseMessage> kafkaTemplate;

  @InjectMocks
  private AppointmentResponseProducer producer;

  @Test
  void send_success() {
    AppointmentResponseMessage message = mock(AppointmentResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.APPOINTMENT_RESPONSE,
        "req-001",
        message
    );
  }
}