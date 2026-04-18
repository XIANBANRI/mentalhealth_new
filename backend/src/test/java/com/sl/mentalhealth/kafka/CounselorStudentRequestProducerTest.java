package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorStudentRequestMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorStudentRequestProducerTest {

  @Mock
  private KafkaTemplate<String, CounselorStudentRequestMessage> kafkaTemplate;

  @InjectMocks
  private CounselorStudentRequestProducer producer;

  @Test
  void send_success() {
    CounselorStudentRequestMessage message = mock(CounselorStudentRequestMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.COUNSELOR_STUDENT_REQUEST,
        "req-001",
        message
    );
  }
}