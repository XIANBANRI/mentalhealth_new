package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.sl.mentalhealth.kafka.message.AssessmentScaleManageResponseMessage;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class PendingAssessmentScaleManageServiceTest {

    private final PendingAssessmentScaleManageService service = new PendingAssessmentScaleManageService();

    @Test
    void create_and_complete_success() {
        CompletableFuture<AssessmentScaleManageResponseMessage> future = service.create("r1");

        AssessmentScaleManageResponseMessage message = new AssessmentScaleManageResponseMessage();
        message.setRequestId("r1");
        message.setMessage("ok");

        service.complete("r1", message);

        assertSame(message, future.join());
    }

    @Test
    void remove_then_complete_futureNotDone() {
        CompletableFuture<AssessmentScaleManageResponseMessage> future = service.create("r2");
        service.remove("r2");

        AssessmentScaleManageResponseMessage message = new AssessmentScaleManageResponseMessage();
        message.setRequestId("r2");
        service.complete("r2", message);

        assertFalse(future.isDone());
    }
}
