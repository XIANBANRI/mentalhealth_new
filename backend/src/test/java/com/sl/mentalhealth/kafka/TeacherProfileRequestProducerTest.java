package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.TeacherProfileRequestMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherProfileRequestProducerTest {

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  @InjectMocks
  private TeacherProfileRequestProducer producer;

  @Test
  void send_success() {
    TeacherProfileRequestMessage message = mock(TeacherProfileRequestMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.TEACHER_PROFILE_REQUEST,
        "req-001",
        message
    );
  }
}