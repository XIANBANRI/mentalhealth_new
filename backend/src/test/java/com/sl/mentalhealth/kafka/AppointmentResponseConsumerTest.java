package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AppointmentResponseMessage;
import com.sl.mentalhealth.service.PendingAppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentResponseConsumerTest {

  @Mock
  private PendingAppointmentService pendingAppointmentService;

  @InjectMocks
  private AppointmentResponseConsumer consumer;

  @Test
  void consume_success() {
    AppointmentResponseMessage message = mock(AppointmentResponseMessage.class);

    consumer.consume(message);

    verify(pendingAppointmentService, times(1)).complete(message);
  }
}