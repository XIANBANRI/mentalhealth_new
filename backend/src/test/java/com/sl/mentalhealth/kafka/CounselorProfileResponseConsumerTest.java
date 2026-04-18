package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorProfileResponseMessage;
import com.sl.mentalhealth.service.PendingCounselorProfileService;
import com.sl.mentalhealth.vo.CounselorProfileResponseVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorProfileResponseConsumerTest {

  @Mock
  private PendingCounselorProfileService pendingCounselorProfileService;

  @InjectMocks
  private CounselorProfileResponseConsumer consumer;

  @Test
  void consume_success() {
    CounselorProfileResponseMessage response = mock(CounselorProfileResponseMessage.class);
    CounselorProfileResponseVO data = mock(CounselorProfileResponseVO.class);

    when(response.isSuccess()).thenReturn(true);
    when(response.getCorrelationId()).thenReturn("corr-001");
    when(response.getData()).thenReturn(data);

    consumer.consume(response);

    verify(pendingCounselorProfileService, times(1))
        .complete("corr-001", data);
    verify(pendingCounselorProfileService, never())
        .completeExceptionally(anyString(), any());
  }

  @Test
  void consume_fail() {
    CounselorProfileResponseMessage response = mock(CounselorProfileResponseMessage.class);

    when(response.isSuccess()).thenReturn(false);
    when(response.getCorrelationId()).thenReturn("corr-002");
    when(response.getMessage()).thenReturn("处理失败");

    consumer.consume(response);

    verify(pendingCounselorProfileService, times(1))
        .completeExceptionally(eq("corr-002"), any(RuntimeException.class));
    verify(pendingCounselorProfileService, never())
        .complete(anyString(), any());
  }
}