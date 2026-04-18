package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentResponseMessage;
import com.sl.mentalhealth.service.PendingAssessmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentResponseConsumerTest {

  @Mock
  private PendingAssessmentService pendingAssessmentService;

  @InjectMocks
  private AssessmentResponseConsumer consumer;

  @Test
  void onMessage_success() {
    AssessmentResponseMessage message = mock(AssessmentResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    consumer.onMessage(message);

    verify(pendingAssessmentService, times(1)).complete("req-001", message);
  }
}