package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.StudentProfileRequestMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentProfileRequestProducerTest {

  @Mock
  private KafkaTemplate<String, StudentProfileRequestMessage> kafkaTemplate;

  @InjectMocks
  private StudentProfileRequestProducer producer;

  @Test
  void send_success() {
    StudentProfileRequestMessage message = mock(StudentProfileRequestMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.STUDENT_PROFILE_REQUEST,
        "req-001",
        message
    );
  }
}