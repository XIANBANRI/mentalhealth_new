package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentScaleManageRequestMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentScaleManageRequestProducerTest {

  @Mock
  private KafkaTemplate<String, AssessmentScaleManageRequestMessage> kafkaTemplate;

  @InjectMocks
  private AssessmentScaleManageRequestProducer producer;

  @Test
  void send_success() {
    AssessmentScaleManageRequestMessage message = mock(AssessmentScaleManageRequestMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.ASSESSMENT_SCALE_MANAGE_REQUEST,
        "req-001",
        message
    );
  }
}