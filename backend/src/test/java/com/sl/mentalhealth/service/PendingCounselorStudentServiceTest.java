package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.kafka.message.CounselorStudentResponseMessage;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PendingCounselorStudentServiceTest {

    private final PendingCounselorStudentService service = new PendingCounselorStudentService();

    @Test
    void create_and_complete_success() {
        CompletableFuture<CounselorStudentResponseMessage> future = service.create("r1");
        CounselorStudentResponseMessage response = Mockito.mock(CounselorStudentResponseMessage.class);
        when(response.getRequestId()).thenReturn("r1");

        service.complete(response);

        assertSame(response, future.join());
    }

    @Test
    void complete_withNullMessage_doesNothing() {
        CompletableFuture<CounselorStudentResponseMessage> future = service.create("r2");

        service.complete(null);

        assertFalse(future.isDone());
    }

    @Test
    void complete_withNullRequestId_doesNothing() {
        CompletableFuture<CounselorStudentResponseMessage> future = service.create("r3");
        CounselorStudentResponseMessage response = Mockito.mock(CounselorStudentResponseMessage.class);
        when(response.getRequestId()).thenReturn(null);

        service.complete(response);

        assertFalse(future.isDone());
    }

    @Test
    void remove_beforeComplete_futureRemainsIncomplete() {
        CompletableFuture<CounselorStudentResponseMessage> future = service.create("r4");
        service.remove("r4");

        CounselorStudentResponseMessage response = Mockito.mock(CounselorStudentResponseMessage.class);
        when(response.getRequestId()).thenReturn("r4");
        service.complete(response);

        assertFalse(future.isDone());
    }
}
