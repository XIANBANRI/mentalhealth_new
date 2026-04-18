package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.TeacherProfileResponseMessage;
import com.sl.mentalhealth.service.PendingTeacherProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherProfileResponseConsumerTest {

  @Mock
  private PendingTeacherProfileService pendingTeacherProfileService;

  @InjectMocks
  private TeacherProfileResponseConsumer consumer;

  @Test
  void consume_success() {
    TeacherProfileResponseMessage message = mock(TeacherProfileResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    consumer.consume(message);

    verify(pendingTeacherProfileService, times(1)).complete("req-001", message);
  }
}