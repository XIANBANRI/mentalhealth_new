package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.ResetPasswordResponseMessage;
import com.sl.mentalhealth.service.PendingResetPasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordResponseConsumerTest {

  @Mock
  private PendingResetPasswordService pendingResetPasswordService;

  @InjectMocks
  private ResetPasswordResponseConsumer consumer;

  @Test
  void onMessage_success() {
    ResetPasswordResponseMessage message = mock(ResetPasswordResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    consumer.onMessage(message);

    verify(pendingResetPasswordService, times(1)).complete("req-001", message);
  }
}