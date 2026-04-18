package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentScaleManageResponseMessage;
import com.sl.mentalhealth.service.PendingAssessmentScaleManageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentScaleManageResponseConsumerTest {

  @Mock
  private PendingAssessmentScaleManageService pendingAssessmentScaleManageService;

  @InjectMocks
  private AssessmentScaleManageResponseConsumer consumer;

  @Test
  void consume_success() {
    AssessmentScaleManageResponseMessage message = mock(AssessmentScaleManageResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    consumer.consume(message);

    verify(pendingAssessmentScaleManageService, times(1)).complete("req-001", message);
  }
}