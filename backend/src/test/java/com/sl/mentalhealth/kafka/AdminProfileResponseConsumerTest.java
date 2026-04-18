package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminProfileResponseMessage;
import com.sl.mentalhealth.service.PendingAdminProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminProfileResponseConsumerTest {

  @Mock
  private PendingAdminProfileService pendingAdminProfileService;

  @InjectMocks
  private AdminProfileResponseConsumer consumer;

  @Test
  void consume_success() {
    AdminProfileResponseMessage message = mock(AdminProfileResponseMessage.class);

    consumer.consume(message);

    verify(pendingAdminProfileService, times(1)).complete(message);
  }

  @Test
  void consume_nullMessage_doNothing() {
    consumer.consume(null);

    verifyNoInteractions(pendingAdminProfileService);
  }
}