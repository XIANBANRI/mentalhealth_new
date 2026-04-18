package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.StudentProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.StudentProfileResponseMessage;
import com.sl.mentalhealth.service.LocalStudentProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentProfileRequestConsumerTest {

  @Mock
  private LocalStudentProfileService localStudentProfileService;

  @Mock
  private StudentProfileResponseProducer studentProfileResponseProducer;

  @InjectMocks
  private StudentProfileRequestConsumer consumer;

  @Test
  void onMessage_success() {
    StudentProfileRequestMessage request = mock(StudentProfileRequestMessage.class);
    StudentProfileResponseMessage response = mock(StudentProfileResponseMessage.class);

    when(localStudentProfileService.handle(request)).thenReturn(response);

    consumer.onMessage(request);

    verify(localStudentProfileService, times(1)).handle(request);
    verify(studentProfileResponseProducer, times(1)).send(response);
  }
}