package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.dto.TeacherAppointmentQueryRequest;
import com.sl.mentalhealth.dto.TeacherAppointmentUpdateStatusRequest;
import com.sl.mentalhealth.dto.TeacherAssessmentRecordQueryRequest;
import com.sl.mentalhealth.kafka.message.TeacherAppointmentRequestMessage;
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
class TeacherAppointmentRequestProducerTest {

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  @InjectMocks
  private TeacherAppointmentRequestProducer producer;

  @Test
  void sendQuery_success() {
    TeacherAppointmentQueryRequest request = mock(TeacherAppointmentQueryRequest.class);

    String requestId = producer.sendQuery(request);

    TeacherAppointmentRequestMessage message = captureMessage();
    assertNotNull(requestId);
    assertEquals(requestId, message.getRequestId());
    assertEquals("QUERY", message.getAction());
    assertSame(request, message.getQueryRequest());
    assertNull(message.getUpdateStatusRequest());
    assertNull(message.getAssessmentRecordQueryRequest());
  }

  @Test
  void sendRecord_success() {
    TeacherAppointmentQueryRequest request = mock(TeacherAppointmentQueryRequest.class);

    String requestId = producer.sendRecord(request);

    TeacherAppointmentRequestMessage message = captureMessage();
    assertNotNull(requestId);
    assertEquals(requestId, message.getRequestId());
    assertEquals("RECORD", message.getAction());
    assertSame(request, message.getQueryRequest());
    assertNull(message.getUpdateStatusRequest());
    assertNull(message.getAssessmentRecordQueryRequest());
  }

  @Test
  void sendUpdateStatus_success() {
    TeacherAppointmentUpdateStatusRequest request = mock(TeacherAppointmentUpdateStatusRequest.class);

    String requestId = producer.sendUpdateStatus(request);

    TeacherAppointmentRequestMessage message = captureMessage();
    assertNotNull(requestId);
    assertEquals(requestId, message.getRequestId());
    assertEquals("UPDATE_STATUS", message.getAction());
    assertNull(message.getQueryRequest());
    assertSame(request, message.getUpdateStatusRequest());
    assertNull(message.getAssessmentRecordQueryRequest());
  }

  @Test
  void sendAssessmentRecord_success() {
    TeacherAssessmentRecordQueryRequest request = mock(TeacherAssessmentRecordQueryRequest.class);

    String requestId = producer.sendAssessmentRecord(request);

    TeacherAppointmentRequestMessage message = captureMessage();
    assertNotNull(requestId);
    assertEquals(requestId, message.getRequestId());
    assertEquals("ASSESSMENT_RECORD", message.getAction());
    assertNull(message.getQueryRequest());
    assertNull(message.getUpdateStatusRequest());
    assertSame(request, message.getAssessmentRecordQueryRequest());
  }

  private TeacherAppointmentRequestMessage captureMessage() {
    ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
    verify(kafkaTemplate, atLeastOnce()).send(
        eq(KafkaTopics.TEACHER_APPOINTMENT_REQUEST),
        anyString(),
        captor.capture()
    );
    return (TeacherAppointmentRequestMessage) captor.getValue();
  }
}