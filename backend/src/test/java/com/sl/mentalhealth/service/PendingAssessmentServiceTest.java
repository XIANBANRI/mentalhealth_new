package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.sl.mentalhealth.kafka.message.AssessmentResponseMessage;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PendingAssessmentServiceTest {

    private final PendingAssessmentService service = new PendingAssessmentService();

    @Test
    void create_and_complete_success() {
        CompletableFuture<AssessmentResponseMessage> future = service.create("r1");
        AssessmentResponseMessage response = Mockito.mock(AssessmentResponseMessage.class);

        service.complete("r1", response);

        assertSame(response, future.join());
    }

    @Test
    void remove_beforeComplete_futureRemainsIncomplete() {
        CompletableFuture<AssessmentResponseMessage> future = service.create("r2");
        service.remove("r2");

        AssessmentResponseMessage response = Mockito.mock(AssessmentResponseMessage.class);
        service.complete("r2", response);

        assertFalse(future.isDone());
    }
}
