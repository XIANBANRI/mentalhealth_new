package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.dto.TeacherScheduleDeleteRequest;
import com.sl.mentalhealth.dto.TeacherScheduleQueryRequest;
import com.sl.mentalhealth.dto.TeacherScheduleSaveRequest;
import com.sl.mentalhealth.kafka.message.TeacherScheduleRequestMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherScheduleRequestProducerTest {

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  @InjectMocks
  private TeacherScheduleRequestProducer producer;

  @Test
  void sendQuery_success() {
    TeacherScheduleQueryRequest request = mock(TeacherScheduleQueryRequest.class);

    String requestId = producer.sendQuery(request);

    TeacherScheduleRequestMessage message = captureMessage();
    assertNotNull(requestId);
    assertEquals(requestId, message.getRequestId());
    assertEquals("QUERY", message.getAction());
    assertSame(request, message.getQueryRequest());
    assertNull(message.getSaveRequest());
    assertNull(message.getDeleteRequest());
  }

  @Test
  void sendAdd_success() {
    TeacherScheduleSaveRequest request = mock(TeacherScheduleSaveRequest.class);

    String requestId = producer.sendAdd(request);

    TeacherScheduleRequestMessage message = captureMessage();
    assertNotNull(requestId);
    assertEquals(requestId, message.getRequestId());
    assertEquals("ADD", message.getAction());
    assertNull(message.getQueryRequest());
    assertSame(request, message.getSaveRequest());
    assertNull(message.getDeleteRequest());
  }

  @Test
  void sendUpdate_success() {
    TeacherScheduleSaveRequest request = mock(TeacherScheduleSaveRequest.class);

    String requestId = producer.sendUpdate(request);

    TeacherScheduleRequestMessage message = captureMessage();
    assertNotNull(requestId);
    assertEquals(requestId, message.getRequestId());
    assertEquals("UPDATE", message.getAction());
    assertNull(message.getQueryRequest());
    assertSame(request, message.getSaveRequest());
    assertNull(message.getDeleteRequest());
  }

  @Test
  void sendDelete_success() {
    TeacherScheduleDeleteRequest request = mock(TeacherScheduleDeleteRequest.class);

    String requestId = producer.sendDelete(request);

    TeacherScheduleRequestMessage message = captureMessage();
    assertNotNull(requestId);
    assertEquals(requestId, message.getRequestId());
    assertEquals("DELETE", message.getAction());
    assertNull(message.getQueryRequest());
    assertNull(message.getSaveRequest());
    assertSame(request, message.getDeleteRequest());
  }

  private TeacherScheduleRequestMessage captureMessage() {
    ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
    verify(kafkaTemplate, atLeastOnce()).send(
        eq(KafkaTopics.TEACHER_SCHEDULE_REQUEST),
        anyString(),
        captor.capture()
    );
    return (TeacherScheduleRequestMessage) captor.getValue();
  }
}