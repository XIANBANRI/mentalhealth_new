package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AssessmentRequestMessage;
import com.sl.mentalhealth.kafka.message.AssessmentResponseMessage;
import com.sl.mentalhealth.service.LocalAssessmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentRequestConsumerTest {

  @Mock
  private LocalAssessmentService localAssessmentService;

  @Mock
  private AssessmentResponseProducer assessmentResponseProducer;

  @InjectMocks
  private AssessmentRequestConsumer consumer;

  @Test
  void onMessage_success() {
    AssessmentRequestMessage message = mock(AssessmentRequestMessage.class);
    AssessmentResponseMessage response = mock(AssessmentResponseMessage.class);

    when(localAssessmentService.handle(message)).thenReturn(response);

    consumer.onMessage(message);

    verify(localAssessmentService, times(1)).handle(message);
    verify(assessmentResponseProducer, times(1)).send(response);
  }
}