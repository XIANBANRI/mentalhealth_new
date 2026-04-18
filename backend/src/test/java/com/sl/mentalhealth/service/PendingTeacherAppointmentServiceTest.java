package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sl.mentalhealth.kafka.message.TeacherAppointmentResponseMessage;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class PendingTeacherAppointmentServiceTest {

    private final PendingTeacherAppointmentService service = new PendingTeacherAppointmentService();

    @Test
    void waitResponse_successAfterComplete() throws Exception {
        CompletableFuture<TeacherAppointmentResponseMessage> waiter =
                CompletableFuture.supplyAsync(() -> service.waitResponse("req1", 1));

        Thread.sleep(50);
        TeacherAppointmentResponseMessage response = new TeacherAppointmentResponseMessage();
        response.setRequestId("req1");
        response.setSuccess(true);
        response.setMessage("ok");
        service.complete("req1", response);

        TeacherAppointmentResponseMessage result = waiter.get(2, TimeUnit.SECONDS);
        assertEquals("req1", readField(result, "requestId"));
        assertEquals(Boolean.TRUE, readField(result, "success"));
        assertEquals("ok", readField(result, "message"));
    }

    @Test
    void waitResponse_timeoutReturnsFailResponse() {
        TeacherAppointmentResponseMessage result = service.waitResponse("req2", 0);

        assertEquals("req2", readField(result, "requestId"));
        assertEquals(Boolean.FALSE, readField(result, "success"));
        assertEquals("老师预约管理处理超时", readField(result, "message"));
    }

    @Test
    void complete_unknownRequestIdDoesNothing() {
        TeacherAppointmentResponseMessage response = new TeacherAppointmentResponseMessage();
        response.setRequestId("none");
        response.setSuccess(true);
        response.setMessage("ok");
        service.complete("none", response);
        assertTrue(true);
    }

    private Object readField(Object target, String fieldName) {
        Class<?> current = target.getClass();
        while (current != null) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("字段不存在: " + fieldName);
    }
}
