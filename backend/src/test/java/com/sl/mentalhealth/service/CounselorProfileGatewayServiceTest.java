package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.kafka.CounselorProfileRequestProducer;
import com.sl.mentalhealth.kafka.message.CounselorProfileRequestMessage;
import com.sl.mentalhealth.vo.CounselorProfileResponseVO;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CounselorProfileGatewayServiceTest {

    @Mock
    private CounselorProfileRequestProducer counselorProfileRequestProducer;

    @Mock
    private PendingCounselorProfileService pendingCounselorProfileService;

    @InjectMocks
    private CounselorProfileGatewayService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "timeoutSeconds", 1L);
    }

    @Test
    void getProfile_success() {
        CounselorProfileResponseVO response = new CounselorProfileResponseVO();

        CompletableFuture<CounselorProfileResponseVO> future = CompletableFuture.completedFuture(response);
        when(pendingCounselorProfileService.create(anyString())).thenReturn(future);

        CounselorProfileResponseVO result = service.getProfile("c001");

        assertSame(response, result);

        ArgumentCaptor<CounselorProfileRequestMessage> captor =
                ArgumentCaptor.forClass(CounselorProfileRequestMessage.class);
        verify(counselorProfileRequestProducer).send(captor.capture());
        CounselorProfileRequestMessage sent = captor.getValue();
        assertEquals(CounselorProfileRequestMessage.ACTION_QUERY_PROFILE, sent.getAction());
        assertEquals("c001", sent.getAccount());
        assertTrue(sent.getCorrelationId() != null && !sent.getCorrelationId().isBlank());
    }

    @Test
    void getProfile_futureException_removesPendingAndThrowsRuntimeException() {
        CompletableFuture<CounselorProfileResponseVO> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("超时"));
        when(pendingCounselorProfileService.create(anyString())).thenReturn(future);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getProfile("c001"));

        assertTrue(ex.getMessage().contains("获取辅导员个人信息超时或失败"));
        verify(pendingCounselorProfileService).remove(anyString());
    }

    @Test
    void updateAvatar_success() {
        CounselorProfileResponseVO response = new CounselorProfileResponseVO();

        CompletableFuture<CounselorProfileResponseVO> future = CompletableFuture.completedFuture(response);
        when(pendingCounselorProfileService.create(anyString())).thenReturn(future);

        CounselorProfileResponseVO result = service.updateAvatar("c001", "/files/avatar/counselor/a.png");

        assertSame(response, result);

        ArgumentCaptor<CounselorProfileRequestMessage> captor =
                ArgumentCaptor.forClass(CounselorProfileRequestMessage.class);
        verify(counselorProfileRequestProducer).send(captor.capture());
        CounselorProfileRequestMessage sent = captor.getValue();
        assertEquals(CounselorProfileRequestMessage.ACTION_UPDATE_AVATAR, sent.getAction());
        assertEquals("c001", sent.getAccount());
        assertEquals("/files/avatar/counselor/a.png", sent.getAvatarUrl());
    }
}
