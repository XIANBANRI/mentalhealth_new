package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorWarningResponseMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorWarningResponseProducerTest {

  @Mock
  private KafkaTemplate<String, CounselorWarningResponseMessage> kafkaTemplate;

  @InjectMocks
  private CounselorWarningResponseProducer producer;

  @Test
  void send_success() {
    CounselorWarningResponseMessage message = mock(CounselorWarningResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.COUNSELOR_WARNING_RESPONSE,
        "req-001",
        message
    );
  }
}