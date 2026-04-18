package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorProfileResponseMessage;
import com.sl.mentalhealth.service.LocalCounselorProfileService;
import com.sl.mentalhealth.vo.CounselorProfileResponseVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorProfileRequestConsumerTest {

  @Mock
  private LocalCounselorProfileService localCounselorProfileService;

  @Mock
  private CounselorProfileResponseProducer counselorProfileResponseProducer;

  @InjectMocks
  private CounselorProfileRequestConsumer consumer;

  @Test
  void consume_updateAvatar_success() {
    CounselorProfileRequestMessage request = mock(CounselorProfileRequestMessage.class);
    CounselorProfileResponseVO data = mock(CounselorProfileResponseVO.class);

    when(request.getAction()).thenReturn(CounselorProfileRequestMessage.ACTION_UPDATE_AVATAR);
    when(request.getAccount()).thenReturn("c001");
    when(request.getAvatarUrl()).thenReturn("/avatar/counselor/a.png");
    when(request.getCorrelationId()).thenReturn("corr-001");

    when(localCounselorProfileService.updateAvatar("c001", "/avatar/counselor/a.png"))
        .thenReturn(data);

    consumer.consume(request);

    verify(localCounselorProfileService, times(1))
        .updateAvatar("c001", "/avatar/counselor/a.png");

    CounselorProfileResponseMessage response = captureResponse();
    assertEquals("corr-001", response.getCorrelationId());
    assertTrue(response.isSuccess());
    assertEquals("头像上传成功", response.getMessage());
    assertSame(data, response.getData());
  }

  @Test
  void consume_updateAvatar_notFound() {
    CounselorProfileRequestMessage request = mock(CounselorProfileRequestMessage.class);

    when(request.getAction()).thenReturn(CounselorProfileRequestMessage.ACTION_UPDATE_AVATAR);
    when(request.getAccount()).thenReturn("c001");
    when(request.getAvatarUrl()).thenReturn("/avatar/counselor/a.png");
    when(request.getCorrelationId()).thenReturn("corr-002");

    when(localCounselorProfileService.updateAvatar("c001", "/avatar/counselor/a.png"))
        .thenReturn(null);

    consumer.consume(request);

    CounselorProfileResponseMessage response = captureResponse();
    assertEquals("corr-002", response.getCorrelationId());
    assertFalse(response.isSuccess());
    assertEquals("未找到辅导员信息", response.getMessage());
    assertNull(response.getData());
  }

  @Test
  void consume_getProfile_success() {
    CounselorProfileRequestMessage request = mock(CounselorProfileRequestMessage.class);
    CounselorProfileResponseVO data = mock(CounselorProfileResponseVO.class);

    when(request.getAction()).thenReturn("GET_PROFILE");
    when(request.getAccount()).thenReturn("c001");
    when(request.getCorrelationId()).thenReturn("corr-003");

    when(localCounselorProfileService.getProfile("c001")).thenReturn(data);

    consumer.consume(request);

    verify(localCounselorProfileService, times(1)).getProfile("c001");

    CounselorProfileResponseMessage response = captureResponse();
    assertEquals("corr-003", response.getCorrelationId());
    assertTrue(response.isSuccess());
    assertEquals("查询成功", response.getMessage());
    assertSame(data, response.getData());
  }

  @Test
  void consume_getProfile_notFound() {
    CounselorProfileRequestMessage request = mock(CounselorProfileRequestMessage.class);

    when(request.getAction()).thenReturn("GET_PROFILE");
    when(request.getAccount()).thenReturn("c001");
    when(request.getCorrelationId()).thenReturn("corr-004");

    when(localCounselorProfileService.getProfile("c001")).thenReturn(null);

    consumer.consume(request);

    CounselorProfileResponseMessage response = captureResponse();
    assertEquals("corr-004", response.getCorrelationId());
    assertFalse(response.isSuccess());
    assertEquals("未找到辅导员信息", response.getMessage());
    assertNull(response.getData());
  }

  @Test
  void consume_exception_failResponse() {
    CounselorProfileRequestMessage request = mock(CounselorProfileRequestMessage.class);

    when(request.getAction()).thenReturn("GET_PROFILE");
    when(request.getAccount()).thenReturn("c001");
    when(request.getCorrelationId()).thenReturn("corr-005");

    when(localCounselorProfileService.getProfile("c001"))
        .thenThrow(new RuntimeException("数据库异常"));

    consumer.consume(request);

    CounselorProfileResponseMessage response = captureResponse();
    assertEquals("corr-005", response.getCorrelationId());
    assertFalse(response.isSuccess());
    assertEquals("处理辅导员信息失败：数据库异常", response.getMessage());
    assertNull(response.getData());
  }

  private CounselorProfileResponseMessage captureResponse() {
    ArgumentCaptor<CounselorProfileResponseMessage> captor =
        ArgumentCaptor.forClass(CounselorProfileResponseMessage.class);
    verify(counselorProfileResponseProducer, atLeastOnce()).send(captor.capture());
    return captor.getValue();
  }
}