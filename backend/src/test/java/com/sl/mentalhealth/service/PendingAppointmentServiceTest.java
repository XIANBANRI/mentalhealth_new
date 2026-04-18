package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.sl.mentalhealth.kafka.message.AppointmentResponseMessage;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class PendingAppointmentServiceTest {

    private final PendingAppointmentService service = new PendingAppointmentService();

    @Test
    void createFuture_and_complete_success() {
        CompletableFuture<AppointmentResponseMessage> future = service.createFuture("r1");

        AppointmentResponseMessage message = new AppointmentResponseMessage();
        message.setRequestId("r1");
        message.setMessage("ok");

        service.complete(message);

        assertSame(message, future.join());
    }

    @Test
    void remove_then_complete_futureNotDone() {
        CompletableFuture<AppointmentResponseMessage> future = service.createFuture("r2");
        service.remove("r2");

        AppointmentResponseMessage message = new AppointmentResponseMessage();
        message.setRequestId("r2");
        service.complete(message);

        assertFalse(future.isDone());
    }
}
