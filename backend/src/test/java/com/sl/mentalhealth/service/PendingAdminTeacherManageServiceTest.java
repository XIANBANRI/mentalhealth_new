package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.kafka.message.AdminTeacherManageResponseMessage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PendingAdminTeacherManageServiceTest {

    private final PendingAdminTeacherManageService service = new PendingAdminTeacherManageService();

    @Mock
    private CompletableFuture<AdminTeacherManageResponseMessage> future;

    @Test
    void create_complete_await_success() {
        CompletableFuture<AdminTeacherManageResponseMessage> created = service.create("r1");

        AdminTeacherManageResponseMessage message = new AdminTeacherManageResponseMessage();
        message.setRequestId("r1");
        message.setMessage("ok");

        service.complete(message);
        AdminTeacherManageResponseMessage result = service.await("r1", created);

        assertSame(message, result);
        assertEquals("ok", result.getMessage());
    }

    @Test
    void await_futureThrows_wrapsException() throws Exception {
        when(future.get(anyLong(), eq(java.util.concurrent.TimeUnit.SECONDS))).thenThrow(new TimeoutException("timeout"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.await("r2", future));
        assertEquals(true, ex.getMessage().startsWith("管理员教师管理请求超时或失败："));
    }
}
