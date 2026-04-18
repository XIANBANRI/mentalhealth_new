package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sl.mentalhealth.vo.CounselorTrendReportVO;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PendingCounselorTrendReportServiceTest {

    private final PendingCounselorTrendReportService service = new PendingCounselorTrendReportService();

    @Test
    void create_complete_and_await_success() {
        service.create("r1");
        CounselorTrendReportVO data = Mockito.mock(CounselorTrendReportVO.class);

        service.complete("r1", data);

        CounselorTrendReportVO result = service.await("r1", 1, TimeUnit.SECONDS);
        assertSame(data, result);
    }

    @Test
    void await_missingRequest_throwsException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.await("missing", 1, TimeUnit.MILLISECONDS));

        org.junit.jupiter.api.Assertions.assertEquals("趋势报告请求不存在或已过期", ex.getMessage());
    }

    @Test
    void await_timeout_throwsFriendlyMessage() {
        service.create("r2");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.await("r2", 1, TimeUnit.MILLISECONDS));

        org.junit.jupiter.api.Assertions.assertEquals("趋势报告查询超时", ex.getMessage());
    }

    @Test
    void completeExceptionally_thenAwait_throwsWrappedMessage() {
        service.create("r3");
        service.completeExceptionally("r3", "查询失败");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.await("r3", 1, TimeUnit.SECONDS));

        assertTrue(ex.getMessage().contains("趋势报告查询失败"));
        assertTrue(ex.getMessage().contains("查询失败"));
    }
}
