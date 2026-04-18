package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.dto.CounselorWarningQueryRequest;
import com.sl.mentalhealth.kafka.CounselorWarningRequestProducer;
import com.sl.mentalhealth.kafka.message.CounselorWarningRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorWarningResponseMessage;
import com.sl.mentalhealth.vo.CounselorWarningDetailVO;
import com.sl.mentalhealth.vo.CounselorWarningPageVO;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CounselorWarningGatewayServiceTest {

    @Mock
    private CounselorWarningRequestProducer counselorWarningRequestProducer;

    @Mock
    private PendingCounselorWarningService pendingCounselorWarningService;

    @InjectMocks
    private CounselorWarningGatewayService service;

    @Test
    void listManagedClasses_success() {
        CounselorWarningResponseMessage response = new CounselorWarningResponseMessage();
        response.setSuccess(true);
        response.setClassOptions(List.of("软件1班"));

        CompletableFuture<CounselorWarningResponseMessage> future = CompletableFuture.completedFuture(response);
        when(pendingCounselorWarningService.create(anyString())).thenReturn(future);

        List<String> result = service.listManagedClasses("c001");

        assertEquals(List.of("软件1班"), result);

        ArgumentCaptor<CounselorWarningRequestMessage> captor =
                ArgumentCaptor.forClass(CounselorWarningRequestMessage.class);
        verify(counselorWarningRequestProducer).send(captor.capture());
        CounselorWarningRequestMessage sent = captor.getValue();
        assertEquals("LIST_CLASSES", sent.getAction());
        assertEquals("c001", sent.getCounselorAccount());
    }

    @Test
    void listDangerousStudents_success() {
        CounselorWarningQueryRequest request = new CounselorWarningQueryRequest();
        request.setCounselorAccount("c001");
        request.setSemester("2025-2026-1");
        request.setClassName("软件1班");
        request.setPageNum(1);
        request.setPageSize(10);

        CounselorWarningPageVO pageVO = new CounselorWarningPageVO();

        CounselorWarningResponseMessage response = new CounselorWarningResponseMessage();
        response.setSuccess(true);
        response.setPageData(pageVO);

        CompletableFuture<CounselorWarningResponseMessage> future = CompletableFuture.completedFuture(response);
        when(pendingCounselorWarningService.create(anyString())).thenReturn(future);

        CounselorWarningPageVO result = service.listDangerousStudents(request);

        assertSame(pageVO, result);

        ArgumentCaptor<CounselorWarningRequestMessage> captor =
                ArgumentCaptor.forClass(CounselorWarningRequestMessage.class);
        verify(counselorWarningRequestProducer).send(captor.capture());
        CounselorWarningRequestMessage sent = captor.getValue();
        assertEquals("LIST", sent.getAction());
        assertEquals("c001", sent.getCounselorAccount());
        assertEquals("2025-2026-1", sent.getSemester());
        assertEquals("软件1班", sent.getClassName());
    }

    @Test
    void getDangerousStudentDetail_responseFail_throwsRuntimeException() {
        CounselorWarningResponseMessage response = new CounselorWarningResponseMessage();
        response.setSuccess(false);
        response.setMessage("预警学生不存在");

        CompletableFuture<CounselorWarningResponseMessage> future = CompletableFuture.completedFuture(response);
        when(pendingCounselorWarningService.create(anyString())).thenReturn(future);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getDangerousStudentDetail("c001", "s001", "2025-2026-1"));

        assertEquals("预警学生不存在", ex.getMessage());
    }

    @Test
    void listManagedClasses_futureException_removesPendingAndThrowsRuntimeException() {
        CompletableFuture<CounselorWarningResponseMessage> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("失败"));
        when(pendingCounselorWarningService.create(anyString())).thenReturn(future);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.listManagedClasses("c001"));

        assertTrue(ex.getMessage().contains("请求处理超时或失败"));
        verify(pendingCounselorWarningService).remove(anyString());
    }
}
