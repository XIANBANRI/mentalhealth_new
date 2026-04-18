package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorStudentRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorStudentResponseMessage;
import com.sl.mentalhealth.service.LocalCounselorStudentService;
import com.sl.mentalhealth.vo.CounselorStudentDetailVO;
import com.sl.mentalhealth.vo.CounselorStudentPageVO;
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
class CounselorStudentRequestConsumerTest {

  @Mock
  private LocalCounselorStudentService localCounselorStudentService;

  @Mock
  private CounselorStudentResponseProducer counselorStudentResponseProducer;

  @InjectMocks
  private CounselorStudentRequestConsumer consumer;

  @Test
  void consume_listClasses_success() {
    CounselorStudentRequestMessage message = mock(CounselorStudentRequestMessage.class);
    List<String> classOptions = Arrays.asList("软件1班", "软件2班");

    when(message.getAction()).thenReturn("LIST_CLASSES");
    when(message.getRequestId()).thenReturn("req-001");
    when(message.getCounselorAccount()).thenReturn("c001");
    when(localCounselorStudentService.listManagedClasses("c001")).thenReturn(classOptions);

    consumer.consume(message);

    verify(localCounselorStudentService, times(1)).listManagedClasses("c001");

    CounselorStudentResponseMessage response = captureResponse();
    assertEquals("req-001", response.getRequestId());
    assertTrue(response.getSuccess());
    assertEquals("查询班级列表成功", response.getMessage());
    assertEquals(classOptions, response.getClassOptions());
  }

  @Test
  void consume_list_success() {
    CounselorStudentRequestMessage message = mock(CounselorStudentRequestMessage.class);
    CounselorStudentPageVO pageVO = mock(CounselorStudentPageVO.class);

    when(message.getAction()).thenReturn("LIST");
    when(message.getRequestId()).thenReturn("req-002");
    when(message.getCounselorAccount()).thenReturn("c001");
    when(message.getClassName()).thenReturn("软件1班");
    when(message.getKeyword()).thenReturn("张三");
    when(message.getPageNum()).thenReturn(1);
    when(message.getPageSize()).thenReturn(10);

    when(localCounselorStudentService.listStudents("c001", "软件1班", "张三", 1, 10))
        .thenReturn(pageVO);

    consumer.consume(message);

    verify(localCounselorStudentService, times(1))
        .listStudents("c001", "软件1班", "张三", 1, 10);

    CounselorStudentResponseMessage response = captureResponse();
    assertEquals("req-002", response.getRequestId());
    assertTrue(response.getSuccess());
    assertEquals("查询成功", response.getMessage());
    assertSame(pageVO, response.getPageData());
  }

  @Test
  void consume_detail_success() {
    CounselorStudentRequestMessage message = mock(CounselorStudentRequestMessage.class);
    CounselorStudentDetailVO detailVO = mock(CounselorStudentDetailVO.class);

    when(message.getAction()).thenReturn("DETAIL");
    when(message.getRequestId()).thenReturn("req-003");
    when(message.getCounselorAccount()).thenReturn("c001");
    when(message.getStudentId()).thenReturn("1001");

    when(localCounselorStudentService.getStudentDetail("c001", "1001"))
        .thenReturn(detailVO);

    consumer.consume(message);

    verify(localCounselorStudentService, times(1))
        .getStudentDetail("c001", "1001");

    CounselorStudentResponseMessage response = captureResponse();
    assertEquals("req-003", response.getRequestId());
    assertTrue(response.getSuccess());
    assertEquals("查询成功", response.getMessage());
    assertSame(detailVO, response.getDetailData());
  }

  @Test
  void consume_unsupportedAction_fail() {
    CounselorStudentRequestMessage message = mock(CounselorStudentRequestMessage.class);

    when(message.getAction()).thenReturn("UNKNOWN");
    when(message.getRequestId()).thenReturn("req-004");

    consumer.consume(message);

    verifyNoInteractions(localCounselorStudentService);

    CounselorStudentResponseMessage response = captureResponse();
    assertEquals("req-004", response.getRequestId());
    assertFalse(response.getSuccess());
    assertEquals("不支持的操作类型：UNKNOWN", response.getMessage());
  }

  @Test
  void consume_exception_fail() {
    CounselorStudentRequestMessage message = mock(CounselorStudentRequestMessage.class);

    when(message.getAction()).thenReturn("LIST_CLASSES");
    when(message.getRequestId()).thenReturn("req-005");
    when(message.getCounselorAccount()).thenReturn("c001");

    when(localCounselorStudentService.listManagedClasses("c001"))
        .thenThrow(new RuntimeException("查询失败"));

    consumer.consume(message);

    CounselorStudentResponseMessage response = captureResponse();
    assertEquals("req-005", response.getRequestId());
    assertFalse(response.getSuccess());
    assertEquals("查询失败", response.getMessage());
  }

  private CounselorStudentResponseMessage captureResponse() {
    ArgumentCaptor<CounselorStudentResponseMessage> captor =
        ArgumentCaptor.forClass(CounselorStudentResponseMessage.class);
    verify(counselorStudentResponseProducer, atLeastOnce()).send(captor.capture());
    return captor.getValue();
  }
}