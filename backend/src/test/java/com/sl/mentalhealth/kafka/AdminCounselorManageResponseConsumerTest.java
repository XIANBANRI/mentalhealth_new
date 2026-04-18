package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminCounselorManageResponseMessage;
import com.sl.mentalhealth.service.PendingAdminCounselorManageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCounselorManageResponseConsumerTest {

  @Mock
  private PendingAdminCounselorManageService pendingService;

  @InjectMocks
  private AdminCounselorManageResponseConsumer consumer;

  @Test
  void consume_success() {
    AdminCounselorManageResponseMessage message = mock(AdminCounselorManageResponseMessage.class);

    consumer.consume(message);

    verify(pendingService, times(1)).complete(message);
  }
}