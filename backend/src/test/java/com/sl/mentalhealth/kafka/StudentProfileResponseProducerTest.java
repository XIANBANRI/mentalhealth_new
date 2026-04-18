package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.StudentProfileResponseMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentProfileResponseProducerTest {

  @Mock
  private KafkaTemplate<String, StudentProfileResponseMessage> kafkaTemplate;

  @InjectMocks
  private StudentProfileResponseProducer producer;

  @Test
  void send_success() {
    StudentProfileResponseMessage message = mock(StudentProfileResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.STUDENT_PROFILE_RESPONSE,
        "req-001",
        message
    );
  }
}