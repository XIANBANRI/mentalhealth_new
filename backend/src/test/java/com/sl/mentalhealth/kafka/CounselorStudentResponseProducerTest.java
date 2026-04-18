package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorStudentResponseMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorStudentResponseProducerTest {

  @Mock
  private KafkaTemplate<String, CounselorStudentResponseMessage> kafkaTemplate;

  @InjectMocks
  private CounselorStudentResponseProducer producer;

  @Test
  void send_success() {
    CounselorStudentResponseMessage message = mock(CounselorStudentResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.COUNSELOR_STUDENT_RESPONSE,
        "req-001",
        message
    );
  }
}