package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.dto.ResetPasswordRequest;
import com.sl.mentalhealth.kafka.ResetPasswordRequestProducer;
import com.sl.mentalhealth.kafka.message.ResetPasswordRequestMessage;
import com.sl.mentalhealth.kafka.message.ResetPasswordResponseMessage;
import com.sl.mentalhealth.vo.ResetPasswordResponseVO;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PasswordGatewayServiceTest {

    @Mock
    private ResetPasswordRequestProducer resetPasswordRequestProducer;

    @Mock
    private PendingResetPasswordService pendingResetPasswordService;

    @InjectMocks
    private PasswordGatewayService service;

    @Test
    void resetPassword_success() {
        ResetPasswordRequest request = org.mockito.Mockito.mock(ResetPasswordRequest.class);
        when(request.getRole()).thenReturn("teacher");
        when(request.getUsername()).thenReturn("t001");
        when(request.getPhone()).thenReturn("13800000000");
        when(request.getPassword()).thenReturn("new123456");

        ResetPasswordResponseMessage response = new ResetPasswordResponseMessage();
        response.setSuccess(true);
        response.setMessage("重置成功");

        CompletableFuture<ResetPasswordResponseMessage> future = CompletableFuture.completedFuture(response);
        when(pendingResetPasswordService.put(anyString())).thenReturn(future);

        ResetPasswordResponseVO result = service.resetPassword(request);

        assertEquals(true, readField(result, "success"));
        assertEquals("重置成功", readField(result, "message"));

        ArgumentCaptor<ResetPasswordRequestMessage> captor = ArgumentCaptor.forClass(ResetPasswordRequestMessage.class);
        verify(resetPasswordRequestProducer).send(captor.capture());
        verify(pendingResetPasswordService).put(anyString());
        assertNotNull(captor.getValue());
    }

    @Test
    void resetPassword_blankField_throwsException() {
        ResetPasswordRequest request = org.mockito.Mockito.mock(ResetPasswordRequest.class);
        when(request.getRole()).thenReturn("teacher");
        when(request.getUsername()).thenReturn(" ");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.resetPassword(request));

        assertEquals("请填写完整信息", ex.getMessage());
        verify(pendingResetPasswordService, never()).put(anyString());
        verify(resetPasswordRequestProducer, never()).send(any());
    }

    @Test
    void resetPassword_responseFail_removesPendingAndThrows() {
        ResetPasswordRequest request = org.mockito.Mockito.mock(ResetPasswordRequest.class);
        when(request.getRole()).thenReturn("teacher");
        when(request.getUsername()).thenReturn("t001");
        when(request.getPhone()).thenReturn("13800000000");
        when(request.getPassword()).thenReturn("new123456");

        ResetPasswordResponseMessage response = new ResetPasswordResponseMessage();
        response.setSuccess(false);
        response.setMessage("手机号不匹配");

        CompletableFuture<ResetPasswordResponseMessage> future = CompletableFuture.completedFuture(response);
        when(pendingResetPasswordService.put(anyString())).thenReturn(future);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.resetPassword(request));

        assertEquals("手机号不匹配", ex.getMessage());
        verify(pendingResetPasswordService).remove(anyString());
    }

    @Test
    void resetPassword_timeout_throwsFriendlyMessage() throws Exception {
        ResetPasswordRequest request = org.mockito.Mockito.mock(ResetPasswordRequest.class);
        when(request.getRole()).thenReturn("teacher");
        when(request.getUsername()).thenReturn("t001");
        when(request.getPhone()).thenReturn("13800000000");
        when(request.getPassword()).thenReturn("new123456");

        @SuppressWarnings("unchecked")
        CompletableFuture<ResetPasswordResponseMessage> future = org.mockito.Mockito.mock(CompletableFuture.class);
        when(pendingResetPasswordService.put(anyString())).thenReturn(future);
        when(future.get(anyLong(), eq(java.util.concurrent.TimeUnit.SECONDS))).thenThrow(new TimeoutException("timeout"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.resetPassword(request));

        assertEquals("重置密码服务超时，请稍后重试", ex.getMessage());
        verify(pendingResetPasswordService).remove(anyString());
    }

    @Test
    void resetPassword_futureException_prefersCauseMessage() {
        ResetPasswordRequest request = org.mockito.Mockito.mock(ResetPasswordRequest.class);
        when(request.getRole()).thenReturn("teacher");
        when(request.getUsername()).thenReturn("t001");
        when(request.getPhone()).thenReturn("13800000000");
        when(request.getPassword()).thenReturn("new123456");

        CompletableFuture<ResetPasswordResponseMessage> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("底层错误"));
        when(pendingResetPasswordService.put(anyString())).thenReturn(future);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.resetPassword(request));

        assertEquals("底层错误", ex.getMessage());
        verify(pendingResetPasswordService).remove(anyString());
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
                throw new RuntimeException("读取字段失败: " + fieldName, e);
            }
        }
        throw new RuntimeException("读取字段失败: " + fieldName);
    }
}