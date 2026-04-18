package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminTeacherManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminTeacherManageResponseMessage;
import com.sl.mentalhealth.service.LocalAdminTeacherManageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminTeacherManageRequestConsumerTest {

  @Mock
  private LocalAdminTeacherManageService localAdminTeacherManageService;

  @Mock
  private AdminTeacherManageResponseProducer responseProducer;

  @InjectMocks
  private AdminTeacherManageRequestConsumer consumer;

  @Test
  void consume_success() {
    AdminTeacherManageRequestMessage requestMessage = mock(AdminTeacherManageRequestMessage.class);
    AdminTeacherManageResponseMessage responseMessage = mock(AdminTeacherManageResponseMessage.class);

    when(localAdminTeacherManageService.handle(requestMessage)).thenReturn(responseMessage);

    consumer.consume(requestMessage);

    verify(localAdminTeacherManageService, times(1)).handle(requestMessage);
    verify(responseProducer, times(1)).send(responseMessage);
  }
}