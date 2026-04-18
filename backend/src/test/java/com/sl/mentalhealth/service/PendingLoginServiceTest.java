package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.sl.mentalhealth.kafka.message.LoginResponseMessage;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PendingLoginServiceTest {

    private final PendingLoginService service = new PendingLoginService();

    @Test
    void put_and_complete_success() {
        CompletableFuture<LoginResponseMessage> future = service.put("r1");
        LoginResponseMessage response = Mockito.mock(LoginResponseMessage.class);

        service.complete("r1", response);

        assertSame(response, future.join());
    }

    @Test
    void remove_beforeComplete_futureRemainsIncomplete() {
        CompletableFuture<LoginResponseMessage> future = service.put("r2");
        service.remove("r2");

        LoginResponseMessage response = Mockito.mock(LoginResponseMessage.class);
        service.complete("r2", response);

        assertFalse(future.isDone());
    }
}
