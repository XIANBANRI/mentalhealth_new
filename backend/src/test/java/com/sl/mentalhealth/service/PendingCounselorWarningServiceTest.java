package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.kafka.message.CounselorWarningResponseMessage;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PendingCounselorWarningServiceTest {

    private final PendingCounselorWarningService service = new PendingCounselorWarningService();

    @Test
    void create_and_complete_success() {
        CompletableFuture<CounselorWarningResponseMessage> future = service.create("r1");
        CounselorWarningResponseMessage response = Mockito.mock(CounselorWarningResponseMessage.class);
        when(response.getRequestId()).thenReturn("r1");

        service.complete(response);

        assertSame(response, future.join());
    }

    @Test
    void complete_withNullMessage_doesNothing() {
        CompletableFuture<CounselorWarningResponseMessage> future = service.create("r2");

        service.complete(null);

        assertFalse(future.isDone());
    }

    @Test
    void complete_withNullRequestId_doesNothing() {
        CompletableFuture<CounselorWarningResponseMessage> future = service.create("r3");
        CounselorWarningResponseMessage response = Mockito.mock(CounselorWarningResponseMessage.class);
        when(response.getRequestId()).thenReturn(null);

        service.complete(response);

        assertFalse(future.isDone());
    }

    @Test
    void remove_beforeComplete_futureRemainsIncomplete() {
        CompletableFuture<CounselorWarningResponseMessage> future = service.create("r4");
        service.remove("r4");

        CounselorWarningResponseMessage response = Mockito.mock(CounselorWarningResponseMessage.class);
        when(response.getRequestId()).thenReturn("r4");
        service.complete(response);

        assertFalse(future.isDone());
    }
}
