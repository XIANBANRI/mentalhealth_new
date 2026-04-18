package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sl.mentalhealth.kafka.message.TeacherProfileResponseMessage;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class PendingTeacherProfileServiceTest {

    private final PendingTeacherProfileService service = new PendingTeacherProfileService();

    @Test
    void createAndComplete_success() throws Exception {
        CompletableFuture<TeacherProfileResponseMessage> future = service.create("req1");

        TeacherProfileResponseMessage response = new TeacherProfileResponseMessage();
        response.setRequestId("req1");
        response.setSuccess(true);
        response.setMessage("ok");

        service.complete("req1", response);

        TeacherProfileResponseMessage result = future.get(1, TimeUnit.SECONDS);
        assertSame(response, result);
    }

    @Test
    void remove_existingRequest() {
        service.create("req2");
        service.remove("req2");
        assertTrue(true);
    }

    @Test
    void buildTimeoutResponse_setsExpectedFields() {
        TeacherProfileResponseMessage result = service.buildTimeoutResponse("req3");

        assertEquals("req3", readField(result, "requestId"));
        assertEquals(Boolean.FALSE, readField(result, "success"));
        assertEquals("老师信息处理超时", readField(result, "message"));
        assertNull(readField(result, "data"));
    }

    @Test
    void buildErrorResponse_setsExpectedFields() {
        TeacherProfileResponseMessage result = service.buildErrorResponse("req4", "失败原因");

        assertEquals("req4", readField(result, "requestId"));
        assertEquals(Boolean.FALSE, readField(result, "success"));
        assertEquals("失败原因", readField(result, "message"));
        assertNull(readField(result, "data"));
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
