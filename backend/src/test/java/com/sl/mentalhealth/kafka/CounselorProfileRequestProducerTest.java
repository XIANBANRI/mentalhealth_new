package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorProfileRequestMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorProfileRequestProducerTest {

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  @InjectMocks
  private CounselorProfileRequestProducer producer;

  @Test
  void send_success() {
    CounselorProfileRequestMessage message = mock(CounselorProfileRequestMessage.class);
    when(message.getCorrelationId()).thenReturn("corr-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.COUNSELOR_PROFILE_REQUEST,
        "corr-001",
        message
    );
  }
}