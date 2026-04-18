package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.sl.mentalhealth.kafka.message.ResetPasswordResponseMessage;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PendingResetPasswordServiceTest {

    private final PendingResetPasswordService service = new PendingResetPasswordService();

    @Test
    void put_and_complete_success() {
        CompletableFuture<ResetPasswordResponseMessage> future = service.put("r1");
        ResetPasswordResponseMessage response = Mockito.mock(ResetPasswordResponseMessage.class);

        service.complete("r1", response);

        assertSame(response, future.join());
    }

    @Test
    void remove_beforeComplete_futureRemainsIncomplete() {
        CompletableFuture<ResetPasswordResponseMessage> future = service.put("r2");
        service.remove("r2");

        ResetPasswordResponseMessage response = Mockito.mock(ResetPasswordResponseMessage.class);
        service.complete("r2", response);

        assertFalse(future.isDone());
    }
}
