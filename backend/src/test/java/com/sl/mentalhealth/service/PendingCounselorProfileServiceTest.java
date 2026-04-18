package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sl.mentalhealth.vo.CounselorProfileResponseVO;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PendingCounselorProfileServiceTest {

    private final PendingCounselorProfileService service = new PendingCounselorProfileService();

    @Test
    void create_and_complete_success() {
        CompletableFuture<CounselorProfileResponseVO> future = service.create("r1");
        CounselorProfileResponseVO response = Mockito.mock(CounselorProfileResponseVO.class);

        service.complete("r1", response);

        assertSame(response, future.join());
    }

    @Test
    void completeExceptionally_marksFutureExceptional() {
        CompletableFuture<CounselorProfileResponseVO> future = service.create("r2");

        service.completeExceptionally("r2", new RuntimeException("test error"));

        CompletionException ex = assertThrows(CompletionException.class, future::join);
        org.junit.jupiter.api.Assertions.assertTrue(ex.getCause().getMessage().contains("test error"));
    }

    @Test
    void remove_beforeComplete_futureRemainsIncomplete() {
        CompletableFuture<CounselorProfileResponseVO> future = service.create("r3");
        service.remove("r3");

        CounselorProfileResponseVO response = Mockito.mock(CounselorProfileResponseVO.class);
        service.complete("r3", response);

        assertFalse(future.isDone());
    }
}
