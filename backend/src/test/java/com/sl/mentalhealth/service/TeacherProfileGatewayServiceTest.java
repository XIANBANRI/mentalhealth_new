package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.kafka.TeacherProfileRequestProducer;
import com.sl.mentalhealth.kafka.message.TeacherProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.TeacherProfileResponseMessage;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TeacherProfileGatewayServiceTest {

    @Mock
    private TeacherProfileRequestProducer teacherProfileRequestProducer;

    @Mock
    private PendingTeacherProfileService pendingTeacherProfileService;

    @InjectMocks
    private TeacherProfileGatewayService service;

    @Test
    void getTeacherProfile_blankAccount_returnsErrorResponse() {
        TeacherProfileResponseMessage error = new TeacherProfileResponseMessage();
        error.setMessage("老师账号不能为空");
        when(pendingTeacherProfileService.buildErrorResponse(null, "老师账号不能为空")).thenReturn(error);

        TeacherProfileResponseMessage result = service.getTeacherProfile(" ");

        assertSame(error, result);
        verify(pendingTeacherProfileService).buildErrorResponse(null, "老师账号不能为空");
        verify(teacherProfileRequestProducer, never()).send(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void getTeacherProfile_success() {
        ReflectionTestUtils.setField(service, "timeoutSeconds", 1L);

        TeacherProfileResponseMessage response = new TeacherProfileResponseMessage();
        response.setRequestId("req1");
        response.setSuccess(true);
        response.setMessage("ok");

        CompletableFuture<TeacherProfileResponseMessage> future = CompletableFuture.completedFuture(response);
        when(pendingTeacherProfileService.create(anyString())).thenReturn(future);

        TeacherProfileResponseMessage result = service.getTeacherProfile(" t001 ");

        assertSame(response, result);

        ArgumentCaptor<TeacherProfileRequestMessage> captor = ArgumentCaptor.forClass(TeacherProfileRequestMessage.class);
        verify(teacherProfileRequestProducer).send(captor.capture());
        TeacherProfileRequestMessage sent = captor.getValue();
        assertNotNull(readField(sent, "requestId"));
        assertEquals(readStaticField(TeacherProfileRequestMessage.class, "ACTION_QUERY_PROFILE"), readField(sent, "action"));
        assertEquals("t001", readField(sent, "teacherAccount"));
        verify(pendingTeacherProfileService).remove(anyString());
    }

    @Test
    void updateAvatar_blankAvatar_returnsErrorResponse() {
        TeacherProfileResponseMessage error = new TeacherProfileResponseMessage();
        error.setMessage("头像地址不能为空");
        when(pendingTeacherProfileService.buildErrorResponse(null, "头像地址不能为空")).thenReturn(error);

        TeacherProfileResponseMessage result = service.updateAvatar("t001", " ");

        assertSame(error, result);
        verify(pendingTeacherProfileService).buildErrorResponse(null, "头像地址不能为空");
        verify(teacherProfileRequestProducer, never()).send(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateAvatar_success() {
        ReflectionTestUtils.setField(service, "timeoutSeconds", 1L);

        TeacherProfileResponseMessage response = new TeacherProfileResponseMessage();
        response.setRequestId("req2");
        response.setSuccess(true);
        response.setMessage("ok");

        CompletableFuture<TeacherProfileResponseMessage> future = CompletableFuture.completedFuture(response);
        when(pendingTeacherProfileService.create(anyString())).thenReturn(future);

        TeacherProfileResponseMessage result = service.updateAvatar(" t001 ", " /avatar/t.png ");

        assertSame(response, result);

        ArgumentCaptor<TeacherProfileRequestMessage> captor = ArgumentCaptor.forClass(TeacherProfileRequestMessage.class);
        verify(teacherProfileRequestProducer).send(captor.capture());
        TeacherProfileRequestMessage sent = captor.getValue();
        assertEquals(readStaticField(TeacherProfileRequestMessage.class, "ACTION_UPDATE_AVATAR"), readField(sent, "action"));
        assertEquals("t001", readField(sent, "teacherAccount"));
        assertEquals("/avatar/t.png", readField(sent, "avatarUrl"));
        verify(pendingTeacherProfileService).remove(anyString());
    }

    @Test
    void sendAndWait_timeout_returnsTimeoutResponse() throws Exception {
        ReflectionTestUtils.setField(service, "timeoutSeconds", 1L);

        TeacherProfileResponseMessage timeoutResponse = new TeacherProfileResponseMessage();
        timeoutResponse.setMessage("老师信息处理超时");

        @SuppressWarnings("unchecked")
        CompletableFuture<TeacherProfileResponseMessage> future = org.mockito.Mockito.mock(CompletableFuture.class);
        when(pendingTeacherProfileService.create(anyString())).thenReturn(future);
        when(future.get(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.eq(TimeUnit.SECONDS)))
                .thenThrow(new TimeoutException("timeout"));
        when(pendingTeacherProfileService.buildTimeoutResponse(anyString())).thenReturn(timeoutResponse);

        TeacherProfileResponseMessage result = service.getTeacherProfile("t001");

        assertSame(timeoutResponse, result);
        verify(pendingTeacherProfileService).buildTimeoutResponse(anyString());
        verify(pendingTeacherProfileService).remove(anyString());
    }

    @Test
    void sendAndWait_exception_returnsErrorResponse() {
        ReflectionTestUtils.setField(service, "timeoutSeconds", 1L);

        TeacherProfileResponseMessage errorResponse = new TeacherProfileResponseMessage();
        errorResponse.setMessage("老师信息处理失败");

        CompletableFuture<TeacherProfileResponseMessage> future = new CompletableFuture<>();
        when(pendingTeacherProfileService.create(anyString())).thenReturn(future);
        org.mockito.Mockito.doThrow(new RuntimeException("send fail"))
                .when(teacherProfileRequestProducer).send(org.mockito.ArgumentMatchers.any());
        when(pendingTeacherProfileService.buildErrorResponse(anyString(), org.mockito.ArgumentMatchers.eq("老师信息处理失败")))
                .thenReturn(errorResponse);

        TeacherProfileResponseMessage result = service.getTeacherProfile("t001");

        assertSame(errorResponse, result);
        verify(pendingTeacherProfileService).buildErrorResponse(anyString(), org.mockito.ArgumentMatchers.eq("老师信息处理失败"));
        verify(pendingTeacherProfileService).remove(anyString());
    }

    private Object readField(Object target, String fieldName) {
        Class<?> current = target.getClass();
        while (current != null) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("字段不存在: " + fieldName);
    }

    private Object readStaticField(Class<?> type, String fieldName) {
        try {
            Field field = type.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
