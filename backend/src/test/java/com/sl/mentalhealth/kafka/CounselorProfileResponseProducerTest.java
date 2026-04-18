package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorProfileResponseMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorProfileResponseProducerTest {

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  @InjectMocks
  private CounselorProfileResponseProducer producer;

  @Test
  void send_success() {
    CounselorProfileResponseMessage message = mock(CounselorProfileResponseMessage.class);
    when(message.getCorrelationId()).thenReturn("corr-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.COUNSELOR_PROFILE_RESPONSE,
        "corr-001",
        message
    );
  }
}