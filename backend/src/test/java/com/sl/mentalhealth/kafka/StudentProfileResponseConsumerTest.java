package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.StudentProfileResponseMessage;
import com.sl.mentalhealth.service.PendingStudentProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentProfileResponseConsumerTest {

  @Mock
  private PendingStudentProfileService pendingStudentProfileService;

  @InjectMocks
  private StudentProfileResponseConsumer consumer;

  @Test
  void onMessage_success() {
    StudentProfileResponseMessage message = mock(StudentProfileResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    consumer.onMessage(message);

    verify(pendingStudentProfileService, times(1)).complete("req-001", message);
  }
}