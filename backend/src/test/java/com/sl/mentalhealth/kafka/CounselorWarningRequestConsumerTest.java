package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorWarningRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorWarningResponseMessage;
import com.sl.mentalhealth.service.LocalCounselorWarningService;
import com.sl.mentalhealth.vo.CounselorWarningDetailVO;
import com.sl.mentalhealth.vo.CounselorWarningPageVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorWarningRequestConsumerTest {

  @Mock
  private LocalCounselorWarningService localCounselorWarningService;

  @Mock
  private CounselorWarningResponseProducer counselorWarningResponseProducer;

  @InjectMocks
  private CounselorWarningRequestConsumer consumer;

  @Test
  void consume_listClasses_success() {
    CounselorWarningRequestMessage message = mock(CounselorWarningRequestMessage.class);
    List<String> classOptions = Arrays.asList("软件1班", "软件2班");

    when(message.getAction()).thenReturn("LIST_CLASSES");
    when(message.getRequestId()).thenReturn("req-001");
    when(message.getCounselorAccount()).thenReturn("c001");
    when(localCounselorWarningService.listManagedClasses("c001")).thenReturn(classOptions);

    consumer.consume(message);

    verify(localCounselorWarningService, times(1)).listManagedClasses("c001");

    CounselorWarningResponseMessage response = captureResponse();
    assertEquals("req-001", response.getRequestId());
    assertTrue(response.getSuccess());
    assertEquals("查询班级列表成功", response.getMessage());
    assertEquals(classOptions, response.getClassOptions());
  }

  @Test
  void consume_list_success() {
    CounselorWarningRequestMessage message = mock(CounselorWarningRequestMessage.class);
    CounselorWarningPageVO pageVO = mock(CounselorWarningPageVO.class);

    when(message.getAction()).thenReturn("LIST");
    when(message.getRequestId()).thenReturn("req-002");
    when(message.getCounselorAccount()).thenReturn("c001");
    when(message.getSemester()).thenReturn("2025-2026-1");
    when(message.getClassName()).thenReturn("软件1班");
    when(message.getPageNum()).thenReturn(1);
    when(message.getPageSize()).thenReturn(10);

    when(localCounselorWarningService.listDangerousStudents("c001", "2025-2026-1", "软件1班", 1, 10))
        .thenReturn(pageVO);

    consumer.consume(message);

    verify(localCounselorWarningService, times(1))
        .listDangerousStudents("c001", "2025-2026-1", "软件1班", 1, 10);

    CounselorWarningResponseMessage response = captureResponse();
    assertEquals("req-002", response.getRequestId());
    assertTrue(response.getSuccess());
    assertEquals("查询预警名单成功", response.getMessage());
    assertSame(pageVO, response.getPageData());
  }

  @Test
  void consume_detail_success() {
    CounselorWarningRequestMessage message = mock(CounselorWarningRequestMessage.class);
    CounselorWarningDetailVO detailVO = mock(CounselorWarningDetailVO.class);

    when(message.getAction()).thenReturn("DETAIL");
    when(message.getRequestId()).thenReturn("req-003");
    when(message.getCounselorAccount()).thenReturn("c001");
    when(message.getStudentId()).thenReturn("1001");
    when(message.getSemester()).thenReturn("2025-2026-1");

    when(localCounselorWarningService.getDangerousStudentDetail("c001", "1001", "2025-2026-1"))
        .thenReturn(detailVO);

    consumer.consume(message);

    verify(localCounselorWarningService, times(1))
        .getDangerousStudentDetail("c001", "1001", "2025-2026-1");

    CounselorWarningResponseMessage response = captureResponse();
    assertEquals("req-003", response.getRequestId());
    assertTrue(response.getSuccess());
    assertEquals("查询预警详情成功", response.getMessage());
    assertSame(detailVO, response.getDetailData());
  }

  @Test
  void consume_unsupportedAction_fail() {
    CounselorWarningRequestMessage message = mock(CounselorWarningRequestMessage.class);

    when(message.getAction()).thenReturn("UNKNOWN");
    when(message.getRequestId()).thenReturn("req-004");

    consumer.consume(message);

    verifyNoInteractions(localCounselorWarningService);

    CounselorWarningResponseMessage response = captureResponse();
    assertEquals("req-004", response.getRequestId());
    assertFalse(response.getSuccess());
    assertEquals("不支持的操作类型：UNKNOWN", response.getMessage());
  }

  @Test
  void consume_exception_fail() {
    CounselorWarningRequestMessage message = mock(CounselorWarningRequestMessage.class);

    when(message.getAction()).thenReturn("LIST_CLASSES");
    when(message.getRequestId()).thenReturn("req-005");
    when(message.getCounselorAccount()).thenReturn("c001");

    when(localCounselorWarningService.listManagedClasses("c001"))
        .thenThrow(new RuntimeException("查询失败"));

    consumer.consume(message);

    CounselorWarningResponseMessage response = captureResponse();
    assertEquals("req-005", response.getRequestId());
    assertFalse(response.getSuccess());
    assertEquals("查询失败", response.getMessage());
  }

  private CounselorWarningResponseMessage captureResponse() {
    ArgumentCaptor<CounselorWarningResponseMessage> captor =
        ArgumentCaptor.forClass(CounselorWarningResponseMessage.class);
    verify(counselorWarningResponseProducer, atLeastOnce()).send(captor.capture());
    return captor.getValue();
  }
}