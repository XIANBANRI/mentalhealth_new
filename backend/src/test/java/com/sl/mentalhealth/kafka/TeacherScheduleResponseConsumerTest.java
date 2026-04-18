package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.TeacherScheduleResponseMessage;
import com.sl.mentalhealth.service.PendingTeacherScheduleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherScheduleResponseConsumerTest {

  @Mock
  private PendingTeacherScheduleService pendingTeacherScheduleService;

  @InjectMocks
  private TeacherScheduleResponseConsumer consumer;

  @Test
  void consume_success() {
    TeacherScheduleResponseMessage message = mock(TeacherScheduleResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    consumer.consume(message);

    verify(pendingTeacherScheduleService, times(1)).complete("req-001", message);
  }
}