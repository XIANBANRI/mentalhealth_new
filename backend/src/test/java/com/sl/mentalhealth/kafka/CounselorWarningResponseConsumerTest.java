package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorWarningResponseMessage;
import com.sl.mentalhealth.service.PendingCounselorWarningService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorWarningResponseConsumerTest {

  @Mock
  private PendingCounselorWarningService pendingCounselorWarningService;

  @InjectMocks
  private CounselorWarningResponseConsumer consumer;

  @Test
  void consume_success() {
    CounselorWarningResponseMessage message = mock(CounselorWarningResponseMessage.class);

    consumer.consume(message);

    verify(pendingCounselorWarningService, times(1)).complete(message);
  }
}