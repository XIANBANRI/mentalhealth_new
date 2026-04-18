package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminCounselorManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminCounselorManageResponseMessage;
import com.sl.mentalhealth.service.LocalAdminCounselorManageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCounselorManageRequestConsumerTest {

  @Mock
  private LocalAdminCounselorManageService localAdminCounselorManageService;

  @Mock
  private AdminCounselorManageResponseProducer responseProducer;

  @InjectMocks
  private AdminCounselorManageRequestConsumer consumer;

  @Test
  void consume_success() {
    AdminCounselorManageRequestMessage requestMessage = mock(AdminCounselorManageRequestMessage.class);
    AdminCounselorManageResponseMessage responseMessage = mock(AdminCounselorManageResponseMessage.class);

    when(localAdminCounselorManageService.handle(requestMessage)).thenReturn(responseMessage);

    consumer.consume(requestMessage);

    verify(localAdminCounselorManageService, times(1)).handle(requestMessage);
    verify(responseProducer, times(1)).send(responseMessage);
  }
}