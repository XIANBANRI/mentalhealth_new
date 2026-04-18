package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentRequestMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentRequestProducerTest {

  @Mock
  private KafkaTemplate<String, AssessmentRequestMessage> kafkaTemplate;

  @InjectMocks
  private AssessmentRequestProducer producer;

  @Test
  void send_success() {
    AssessmentRequestMessage message = mock(AssessmentRequestMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.ASSESSMENT_REQUEST,
        "req-001",
        message
    );
  }
}