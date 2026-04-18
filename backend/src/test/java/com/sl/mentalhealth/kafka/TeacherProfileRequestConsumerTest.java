package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.TeacherProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.TeacherProfileResponseMessage;
import com.sl.mentalhealth.service.LocalTeacherProfileService;
import com.sl.mentalhealth.vo.TeacherProfileResponseVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherProfileRequestConsumerTest {

  @Mock
  private LocalTeacherProfileService localTeacherProfileService;

  @Mock
  private TeacherProfileResponseProducer teacherProfileResponseProducer;

  @InjectMocks
  private TeacherProfileRequestConsumer consumer;

  @Test
  void consume_updateAvatar_success() {
    TeacherProfileRequestMessage message = mock(TeacherProfileRequestMessage.class);
    TeacherProfileResponseVO data = mock(TeacherProfileResponseVO.class);

    when(message.getRequestId()).thenReturn("req-001");
    when(message.getAction()).thenReturn(TeacherProfileRequestMessage.ACTION_UPDATE_AVATAR);
    when(message.getTeacherAccount()).thenReturn("t001");
    when(message.getAvatarUrl()).thenReturn("/avatar/teacher/a.png");
    when(localTeacherProfileService.updateAvatar("t001", "/avatar/teacher/a.png")).thenReturn(data);

    consumer.consume(message);

    verify(localTeacherProfileService, times(1)).updateAvatar("t001", "/avatar/teacher/a.png");
    verify(localTeacherProfileService, never()).getTeacherProfile(anyString());

    TeacherProfileResponseMessage response = captureResponse();
    assertEquals("req-001", response.getRequestId());
    assertEquals(Boolean.TRUE, ReflectionTestUtils.getField(response, "success"));
    assertEquals("头像上传成功", ReflectionTestUtils.getField(response, "message"));
    assertSame(data, ReflectionTestUtils.getField(response, "data"));
  }

  @Test
  void consume_getProfile_success() {
    TeacherProfileRequestMessage message = mock(TeacherProfileRequestMessage.class);
    TeacherProfileResponseVO data = mock(TeacherProfileResponseVO.class);

    when(message.getRequestId()).thenReturn("req-002");
    when(message.getAction()).thenReturn("GET_PROFILE");
    when(message.getTeacherAccount()).thenReturn("t001");
    when(localTeacherProfileService.getTeacherProfile("t001")).thenReturn(data);

    consumer.consume(message);

    verify(localTeacherProfileService, times(1)).getTeacherProfile("t001");
    verify(localTeacherProfileService, never()).updateAvatar(anyString(), anyString());

    TeacherProfileResponseMessage response = captureResponse();
    assertEquals("req-002", response.getRequestId());
    assertEquals(Boolean.TRUE, ReflectionTestUtils.getField(response, "success"));
    assertEquals("查询成功", ReflectionTestUtils.getField(response, "message"));
    assertSame(data, ReflectionTestUtils.getField(response, "data"));
  }

  @Test
  void consume_exception_fail() {
    TeacherProfileRequestMessage message = mock(TeacherProfileRequestMessage.class);

    when(message.getRequestId()).thenReturn("req-003");
    when(message.getAction()).thenReturn("GET_PROFILE");
    when(message.getTeacherAccount()).thenReturn("t001");
    when(localTeacherProfileService.getTeacherProfile("t001"))
        .thenThrow(new RuntimeException("查询失败"));

    consumer.consume(message);

    TeacherProfileResponseMessage response = captureResponse();
    assertEquals("req-003", response.getRequestId());
    assertEquals(Boolean.FALSE, ReflectionTestUtils.getField(response, "success"));
    assertEquals("查询失败", ReflectionTestUtils.getField(response, "message"));
    assertNull(ReflectionTestUtils.getField(response, "data"));
  }

  private TeacherProfileResponseMessage captureResponse() {
    ArgumentCaptor<TeacherProfileResponseMessage> captor =
        ArgumentCaptor.forClass(TeacherProfileResponseMessage.class);
    verify(teacherProfileResponseProducer, atLeastOnce()).send(captor.capture());
    return captor.getValue();
  }
}