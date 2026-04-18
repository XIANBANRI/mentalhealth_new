package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.kafka.message.AdminCounselorManageResponseMessage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PendingAdminCounselorManageServiceTest {

    private final PendingAdminCounselorManageService service = new PendingAdminCounselorManageService();

    @Mock
    private CompletableFuture<AdminCounselorManageResponseMessage> future;

    @Test
    void create_complete_await_success() {
        CompletableFuture<AdminCounselorManageResponseMessage> created = service.create("r1");

        AdminCounselorManageResponseMessage message = new AdminCounselorManageResponseMessage();
        message.setRequestId("r1");
        message.setMessage("ok");

        service.complete(message);
        AdminCounselorManageResponseMessage result = service.await("r1", created);

        assertSame(message, result);
        assertEquals("ok", result.getMessage());
    }

    @Test
    void complete_nullMessage_noException() {
        service.complete(null);
    }

    @Test
    void await_futureThrows_wrapsException() throws Exception {
        when(future.get(anyLong(), eq(java.util.concurrent.TimeUnit.SECONDS))).thenThrow(new TimeoutException("timeout"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.await("r2", future));
        assertEquals(true, ex.getMessage().startsWith("管理员辅导员管理请求超时或失败："));
    }
}
