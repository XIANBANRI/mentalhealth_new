package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.LoginResponseMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginResponseProducerTest {

  @Mock
  private KafkaTemplate<String, LoginResponseMessage> kafkaTemplate;

  @InjectMocks
  private LoginResponseProducer producer;

  @Test
  void send_success() {
    LoginResponseMessage message = mock(LoginResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.LOGIN_RESPONSE,
        "req-001",
        message
    );
  }
}