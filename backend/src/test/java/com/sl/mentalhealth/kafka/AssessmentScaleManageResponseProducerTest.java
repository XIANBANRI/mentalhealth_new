package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentScaleManageResponseMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentScaleManageResponseProducerTest {

  @Mock
  private KafkaTemplate<String, AssessmentScaleManageResponseMessage> kafkaTemplate;

  @InjectMocks
  private AssessmentScaleManageResponseProducer producer;

  @Test
  void send_success() {
    AssessmentScaleManageResponseMessage message = mock(AssessmentScaleManageResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.ASSESSMENT_SCALE_MANAGE_RESPONSE,
        "req-001",
        message
    );
  }
}