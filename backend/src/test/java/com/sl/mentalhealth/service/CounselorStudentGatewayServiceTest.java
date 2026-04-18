package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.dto.CounselorStudentQueryRequest;
import com.sl.mentalhealth.kafka.CounselorStudentRequestProducer;
import com.sl.mentalhealth.kafka.message.CounselorStudentRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorStudentResponseMessage;
import com.sl.mentalhealth.vo.CounselorStudentDetailVO;
import com.sl.mentalhealth.vo.CounselorStudentPageVO;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CounselorStudentGatewayServiceTest {

    @Mock
    private CounselorStudentRequestProducer counselorStudentRequestProducer;

    @Mock
    private PendingCounselorStudentService pendingCounselorStudentService;

    @InjectMocks
    private CounselorStudentGatewayService service;

    @Test
    void listManagedClasses_success() {
        CounselorStudentResponseMessage response = new CounselorStudentResponseMessage();
        response.setSuccess(true);
        response.setClassOptions(List.of("软件1班", "软件2班"));

        CompletableFuture<CounselorStudentResponseMessage> future = CompletableFuture.completedFuture(response);
        when(pendingCounselorStudentService.create(anyString())).thenReturn(future);

        List<String> result = service.listManagedClasses("c001");

        assertEquals(List.of("软件1班", "软件2班"), result);

        ArgumentCaptor<CounselorStudentRequestMessage> captor =
                ArgumentCaptor.forClass(CounselorStudentRequestMessage.class);
        verify(counselorStudentRequestProducer).send(captor.capture());
        CounselorStudentRequestMessage sent = captor.getValue();
        assertEquals("LIST_CLASSES", sent.getAction());
        assertEquals("c001", sent.getCounselorAccount());
    }

    @Test
    void listStudents_success() {
        CounselorStudentQueryRequest request = new CounselorStudentQueryRequest();
        request.setCounselorAccount("c001");
        request.setClassName("软件1班");
        request.setKeyword("张");
        request.setPageNum(1);
        request.setPageSize(10);

        CounselorStudentPageVO pageVO = new CounselorStudentPageVO();

        CounselorStudentResponseMessage response = new CounselorStudentResponseMessage();
        response.setSuccess(true);
        response.setPageData(pageVO);

        CompletableFuture<CounselorStudentResponseMessage> future = CompletableFuture.completedFuture(response);
        when(pendingCounselorStudentService.create(anyString())).thenReturn(future);

        CounselorStudentPageVO result = service.listStudents(request);

        assertSame(pageVO, result);

        ArgumentCaptor<CounselorStudentRequestMessage> captor =
                ArgumentCaptor.forClass(CounselorStudentRequestMessage.class);
        verify(counselorStudentRequestProducer).send(captor.capture());
        CounselorStudentRequestMessage sent = captor.getValue();
        assertEquals("LIST", sent.getAction());
        assertEquals("c001", sent.getCounselorAccount());
        assertEquals("软件1班", sent.getClassName());
        assertEquals("张", sent.getKeyword());
        assertEquals(1, sent.getPageNum());
        assertEquals(10, sent.getPageSize());
    }

    @Test
    void getStudentDetail_responseFail_throwsRuntimeException() {
        CounselorStudentResponseMessage response = new CounselorStudentResponseMessage();
        response.setSuccess(false);
        response.setMessage("学生不存在");

        CompletableFuture<CounselorStudentResponseMessage> future = CompletableFuture.completedFuture(response);
        when(pendingCounselorStudentService.create(anyString())).thenReturn(future);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getStudentDetail("c001", "s001"));

        assertEquals("学生不存在", ex.getMessage());
    }

    @Test
    void listManagedClasses_futureException_removesPendingAndThrowsRuntimeException() {
        CompletableFuture<CounselorStudentResponseMessage> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("失败"));
        when(pendingCounselorStudentService.create(anyString())).thenReturn(future);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.listManagedClasses("c001"));

        assertTrue(ex.getMessage().contains("请求处理超时或失败"));
        verify(pendingCounselorStudentService).remove(anyString());
    }
}
