package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.TeacherAppointmentResponseMessage;
import com.sl.mentalhealth.service.PendingTeacherAppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherAppointmentResponseConsumerTest {

  @Mock
  private PendingTeacherAppointmentService pendingTeacherAppointmentService;

  @InjectMocks
  private TeacherAppointmentResponseConsumer consumer;

  @Test
  void consume_success() {
    TeacherAppointmentResponseMessage message = mock(TeacherAppointmentResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    consumer.consume(message);

    verify(pendingTeacherAppointmentService, times(1)).complete("req-001", message);
  }
}