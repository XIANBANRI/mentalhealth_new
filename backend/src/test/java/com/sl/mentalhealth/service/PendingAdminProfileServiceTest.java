package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.sl.mentalhealth.kafka.message.AdminProfileResponseMessage;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class PendingAdminProfileServiceTest {

    private final PendingAdminProfileService service = new PendingAdminProfileService();

    @Test
    void create_and_complete_success() {
        CompletableFuture<AdminProfileResponseMessage> future = service.create("r1");

        AdminProfileResponseMessage message = new AdminProfileResponseMessage();
        message.setRequestId("r1");
        message.setMessage("ok");

        service.complete(message);

        assertSame(message, future.join());
    }

    @Test
    void remove_beforeComplete_futureRemainsIncomplete() {
        CompletableFuture<AdminProfileResponseMessage> future = service.create("r2");
        service.remove("r2");

        AdminProfileResponseMessage message = new AdminProfileResponseMessage();
        message.setRequestId("r2");
        service.complete(message);

        assertFalse(future.isDone());
    }
}
