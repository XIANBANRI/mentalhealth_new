package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorWarningRequestMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorWarningRequestProducerTest {

  @Mock
  private KafkaTemplate<String, CounselorWarningRequestMessage> kafkaTemplate;

  @InjectMocks
  private CounselorWarningRequestProducer producer;

  @Test
  void send_success() {
    CounselorWarningRequestMessage message = mock(CounselorWarningRequestMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.COUNSELOR_WARNING_REQUEST,
        "req-001",
        message
    );
  }
}