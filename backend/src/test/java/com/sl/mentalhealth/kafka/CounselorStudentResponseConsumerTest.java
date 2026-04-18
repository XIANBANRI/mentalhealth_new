package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorStudentResponseMessage;
import com.sl.mentalhealth.service.PendingCounselorStudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorStudentResponseConsumerTest {

  @Mock
  private PendingCounselorStudentService pendingCounselorStudentService;

  @InjectMocks
  private CounselorStudentResponseConsumer consumer;

  @Test
  void consume_success() {
    CounselorStudentResponseMessage message = mock(CounselorStudentResponseMessage.class);

    consumer.consume(message);

    verify(pendingCounselorStudentService, times(1)).complete(message);
  }
}