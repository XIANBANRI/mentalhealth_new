package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sl.mentalhealth.kafka.message.TeacherScheduleResponseMessage;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class PendingTeacherScheduleServiceTest {

    private final PendingTeacherScheduleService service = new PendingTeacherScheduleService();

    @Test
    void waitResponse_successAfterComplete() throws Exception {
        CompletableFuture<TeacherScheduleResponseMessage> waiter =
                CompletableFuture.supplyAsync(() -> service.waitResponse("req1", 1));

        Thread.sleep(50);
        TeacherScheduleResponseMessage response = new TeacherScheduleResponseMessage();
        response.setRequestId("req1");
        response.setSuccess(true);
        response.setMessage("ok");
        service.complete("req1", response);

        TeacherScheduleResponseMessage result = waiter.get(2, TimeUnit.SECONDS);
        assertEquals("req1", readField(result, "requestId"));
        assertEquals(Boolean.TRUE, readField(result, "success"));
        assertEquals("ok", readField(result, "message"));
    }

    @Test
    void waitResponse_timeoutReturnsFailResponse() {
        TeacherScheduleResponseMessage result = service.waitResponse("req2", 0);

        assertEquals("req2", readField(result, "requestId"));
        assertEquals(Boolean.FALSE, readField(result, "success"));
        assertEquals("老师工作时间处理超时", readField(result, "message"));
    }

    @Test
    void complete_unknownRequestIdDoesNothing() {
        TeacherScheduleResponseMessage response = new TeacherScheduleResponseMessage();
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
