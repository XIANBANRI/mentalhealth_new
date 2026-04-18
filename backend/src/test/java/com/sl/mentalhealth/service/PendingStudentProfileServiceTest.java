package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.sl.mentalhealth.kafka.message.StudentProfileResponseMessage;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PendingStudentProfileServiceTest {

    private final PendingStudentProfileService service = new PendingStudentProfileService();

    @Test
    void create_and_complete_success() {
        CompletableFuture<StudentProfileResponseMessage> future = service.create("r1");
        StudentProfileResponseMessage response = Mockito.mock(StudentProfileResponseMessage.class);

        service.complete("r1", response);

        assertSame(response, future.join());
    }

    @Test
    void remove_beforeComplete_futureRemainsIncomplete() {
        CompletableFuture<StudentProfileResponseMessage> future = service.create("r2");
        service.remove("r2");

        StudentProfileResponseMessage response = Mockito.mock(StudentProfileResponseMessage.class);
        service.complete("r2", response);

        assertFalse(future.isDone());
    }
}
