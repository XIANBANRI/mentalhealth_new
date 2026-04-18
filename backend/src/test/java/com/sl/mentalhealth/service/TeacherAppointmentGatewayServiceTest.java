package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.dto.TeacherAppointmentQueryRequest;
import com.sl.mentalhealth.dto.TeacherAppointmentUpdateStatusRequest;
import com.sl.mentalhealth.dto.TeacherAssessmentRecordQueryRequest;
import com.sl.mentalhealth.kafka.TeacherAppointmentRequestProducer;
import com.sl.mentalhealth.kafka.message.TeacherAppointmentResponseMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeacherAppointmentGatewayServiceTest {

    @Mock
    private TeacherAppointmentRequestProducer teacherAppointmentRequestProducer;

    @Mock
    private PendingTeacherAppointmentService pendingTeacherAppointmentService;

    @InjectMocks
    private TeacherAppointmentGatewayService service;

    @Test
    void query_delegatesToProducerAndPendingService() {
        TeacherAppointmentQueryRequest request = org.mockito.Mockito.mock(TeacherAppointmentQueryRequest.class);
        TeacherAppointmentResponseMessage response = new TeacherAppointmentResponseMessage();

        when(teacherAppointmentRequestProducer.sendQuery(request)).thenReturn("req1");
        when(pendingTeacherAppointmentService.waitResponse("req1", 8)).thenReturn(response);

        TeacherAppointmentResponseMessage result = service.query(request);

        assertSame(response, result);
        verify(teacherAppointmentRequestProducer).sendQuery(request);
        verify(pendingTeacherAppointmentService).waitResponse("req1", 8);
    }

    @Test
    void record_delegatesToProducerAndPendingService() {
        TeacherAppointmentQueryRequest request = org.mockito.Mockito.mock(TeacherAppointmentQueryRequest.class);
        TeacherAppointmentResponseMessage response = new TeacherAppointmentResponseMessage();

        when(teacherAppointmentRequestProducer.sendRecord(request)).thenReturn("req2");
        when(pendingTeacherAppointmentService.waitResponse("req2", 8)).thenReturn(response);

        TeacherAppointmentResponseMessage result = service.record(request);

        assertSame(response, result);
        verify(teacherAppointmentRequestProducer).sendRecord(request);
        verify(pendingTeacherAppointmentService).waitResponse("req2", 8);
    }

    @Test
    void updateStatus_delegatesToProducerAndPendingService() {
        TeacherAppointmentUpdateStatusRequest request = org.mockito.Mockito.mock(TeacherAppointmentUpdateStatusRequest.class);
        TeacherAppointmentResponseMessage response = new TeacherAppointmentResponseMessage();

        when(teacherAppointmentRequestProducer.sendUpdateStatus(request)).thenReturn("req3");
        when(pendingTeacherAppointmentService.waitResponse("req3", 8)).thenReturn(response);

        TeacherAppointmentResponseMessage result = service.updateStatus(request);

        assertSame(response, result);
        verify(teacherAppointmentRequestProducer).sendUpdateStatus(request);
        verify(pendingTeacherAppointmentService).waitResponse("req3", 8);
    }

    @Test
    void assessmentRecord_delegatesToProducerAndPendingService() {
        TeacherAssessmentRecordQueryRequest request = org.mockito.Mockito.mock(TeacherAssessmentRecordQueryRequest.class);
        TeacherAppointmentResponseMessage response = new TeacherAppointmentResponseMessage();

        when(teacherAppointmentRequestProducer.sendAssessmentRecord(request)).thenReturn("req4");
        when(pendingTeacherAppointmentService.waitResponse("req4", 8)).thenReturn(response);

        TeacherAppointmentResponseMessage result = service.assessmentRecord(request);

        assertSame(response, result);
        verify(teacherAppointmentRequestProducer).sendAssessmentRecord(request);
        verify(pendingTeacherAppointmentService).waitResponse("req4", 8);
    }
}
