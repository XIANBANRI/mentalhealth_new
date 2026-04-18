package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.LoginResponseMessage;
import com.sl.mentalhealth.service.PendingLoginService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginResponseConsumerTest {

  @Mock
  private PendingLoginService pendingLoginService;

  @InjectMocks
  private LoginResponseConsumer consumer;

  @Test
  void onMessage_success() {
    LoginResponseMessage message = mock(LoginResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    consumer.onMessage(message);

    verify(pendingLoginService, times(1)).complete("req-001", message);
  }
}