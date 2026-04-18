package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.dto.TeacherScheduleDeleteRequest;
import com.sl.mentalhealth.dto.TeacherScheduleQueryRequest;
import com.sl.mentalhealth.dto.TeacherScheduleSaveRequest;
import com.sl.mentalhealth.kafka.TeacherScheduleRequestProducer;
import com.sl.mentalhealth.kafka.message.TeacherScheduleResponseMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeacherScheduleGatewayServiceTest {

    @Mock
    private TeacherScheduleRequestProducer teacherScheduleRequestProducer;

    @Mock
    private PendingTeacherScheduleService pendingTeacherScheduleService;

    @InjectMocks
    private TeacherScheduleGatewayService service;

    @Test
    void query_delegatesToProducerAndPendingService() {
        TeacherScheduleQueryRequest request = org.mockito.Mockito.mock(TeacherScheduleQueryRequest.class);
        TeacherScheduleResponseMessage response = new TeacherScheduleResponseMessage();

        when(teacherScheduleRequestProducer.sendQuery(request)).thenReturn("req1");
        when(pendingTeacherScheduleService.waitResponse("req1", 8)).thenReturn(response);

        TeacherScheduleResponseMessage result = service.query(request);

        assertSame(response, result);
        verify(teacherScheduleRequestProducer).sendQuery(request);
        verify(pendingTeacherScheduleService).waitResponse("req1", 8);
    }

    @Test
    void add_delegatesToProducerAndPendingService() {
        TeacherScheduleSaveRequest request = org.mockito.Mockito.mock(TeacherScheduleSaveRequest.class);
        TeacherScheduleResponseMessage response = new TeacherScheduleResponseMessage();

        when(teacherScheduleRequestProducer.sendAdd(request)).thenReturn("req2");
        when(pendingTeacherScheduleService.waitResponse("req2", 8)).thenReturn(response);

        TeacherScheduleResponseMessage result = service.add(request);

        assertSame(response, result);
        verify(teacherScheduleRequestProducer).sendAdd(request);
        verify(pendingTeacherScheduleService).waitResponse("req2", 8);
    }

    @Test
    void update_delegatesToProducerAndPendingService() {
        TeacherScheduleSaveRequest request = org.mockito.Mockito.mock(TeacherScheduleSaveRequest.class);
        TeacherScheduleResponseMessage response = new TeacherScheduleResponseMessage();

        when(teacherScheduleRequestProducer.sendUpdate(request)).thenReturn("req3");
        when(pendingTeacherScheduleService.waitResponse("req3", 8)).thenReturn(response);

        TeacherScheduleResponseMessage result = service.update(request);

        assertSame(response, result);
        verify(teacherScheduleRequestProducer).sendUpdate(request);
        verify(pendingTeacherScheduleService).waitResponse("req3", 8);
    }

    @Test
    void delete_delegatesToProducerAndPendingService() {
        TeacherScheduleDeleteRequest request = org.mockito.Mockito.mock(TeacherScheduleDeleteRequest.class);
        TeacherScheduleResponseMessage response = new TeacherScheduleResponseMessage();

        when(teacherScheduleRequestProducer.sendDelete(request)).thenReturn("req4");
        when(pendingTeacherScheduleService.waitResponse("req4", 8)).thenReturn(response);

        TeacherScheduleResponseMessage result = service.delete(request);

        assertSame(response, result);
        verify(teacherScheduleRequestProducer).sendDelete(request);
        verify(pendingTeacherScheduleService).waitResponse("req4", 8);
    }
}
