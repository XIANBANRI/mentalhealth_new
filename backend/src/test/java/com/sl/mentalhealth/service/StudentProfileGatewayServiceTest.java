package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.dto.StudentProfileRequest;
import com.sl.mentalhealth.kafka.StudentProfileRequestProducer;
import com.sl.mentalhealth.kafka.message.StudentProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.StudentProfileResponseMessage;
import com.sl.mentalhealth.vo.StudentProfileResponseVO;
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
class StudentProfileGatewayServiceTest {

    @Mock
    private StudentProfileRequestProducer studentProfileRequestProducer;

    @Mock
    private PendingStudentProfileService pendingStudentProfileService;

    @InjectMocks
    private StudentProfileGatewayService service;

    @Test
    void queryProfile_success() {
        ReflectionTestUtils.setField(service, "timeoutSeconds", 1L);

        StudentProfileRequest request = org.mockito.Mockito.mock(StudentProfileRequest.class);
        when(request.getStudentId()).thenReturn(" s001 ");

        StudentProfileResponseMessage response = new StudentProfileResponseMessage();
        response.setSuccess(true);
        response.setStudentId("s001");
        response.setName("张三");
        response.setClassName("软件1班");
        response.setCollege("计算机学院");
        response.setGrade("2023");
        response.setPhone("13800000000");
        response.setAvatarUrl("/avatar/a.png");
        response.setCounselorName("李老师");
        response.setCounselorPhone("13900000000");

        CompletableFuture<StudentProfileResponseMessage> future = CompletableFuture.completedFuture(response);
        when(pendingStudentProfileService.create(anyString())).thenReturn(future);

        StudentProfileResponseVO result = service.queryProfile(request);

        assertEquals("s001", readField(result, "studentId"));
        assertEquals("张三", readField(result, "name"));
        assertEquals("软件1班", readField(result, "className"));

        ArgumentCaptor<StudentProfileRequestMessage> captor = ArgumentCaptor.forClass(StudentProfileRequestMessage.class);
        verify(studentProfileRequestProducer).send(captor.capture());
        StudentProfileRequestMessage sent = captor.getValue();
        assertNotNull(readField(sent, "requestId"));
        assertEquals(readStaticField(StudentProfileRequestMessage.class, "ACTION_QUERY_PROFILE"), readField(sent, "action"));
        assertEquals("s001", readField(sent, "studentId"));
        verify(pendingStudentProfileService).remove(anyString());
    }

    @Test
    void queryProfile_blankStudentId_throwsException() {
        StudentProfileRequest request = org.mockito.Mockito.mock(StudentProfileRequest.class);
        when(request.getStudentId()).thenReturn(" ");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.queryProfile(request));

        assertEquals("学号不能为空", ex.getMessage());
        verify(studentProfileRequestProducer, never()).send(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateAvatar_success() {
        ReflectionTestUtils.setField(service, "timeoutSeconds", 1L);

        StudentProfileResponseMessage response = new StudentProfileResponseMessage();
        response.setSuccess(true);
        response.setStudentId("s001");
        response.setName("张三");

        CompletableFuture<StudentProfileResponseMessage> future = CompletableFuture.completedFuture(response);
        when(pendingStudentProfileService.create(anyString())).thenReturn(future);

        StudentProfileResponseVO result = service.updateAvatar(" s001 ", " /avatar/new.png ");

        assertEquals("s001", readField(result, "studentId"));
        assertEquals("张三", readField(result, "name"));

        ArgumentCaptor<StudentProfileRequestMessage> captor = ArgumentCaptor.forClass(StudentProfileRequestMessage.class);
        verify(studentProfileRequestProducer).send(captor.capture());
        StudentProfileRequestMessage sent = captor.getValue();
        assertEquals(readStaticField(StudentProfileRequestMessage.class, "ACTION_UPDATE_AVATAR"), readField(sent, "action"));
        assertEquals("s001", readField(sent, "studentId"));
        assertEquals("/avatar/new.png", readField(sent, "avatarUrl"));
        verify(pendingStudentProfileService).remove(anyString());
    }

    @Test
    void updateAvatar_blankStudentId_throwsException() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.updateAvatar(" ", "/a.png"));
        assertEquals("学号不能为空", ex.getMessage());
    }

    @Test
    void updateAvatar_blankAvatar_throwsException() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.updateAvatar("s001", " "));
        assertEquals("头像地址不能为空", ex.getMessage());
    }

    @Test
    void sendAndWait_responseFail_throwsResponseMessage() {
        ReflectionTestUtils.setField(service, "timeoutSeconds", 1L);

        StudentProfileRequest request = org.mockito.Mockito.mock(StudentProfileRequest.class);
        when(request.getStudentId()).thenReturn("s001");

        StudentProfileResponseMessage response = new StudentProfileResponseMessage();
        response.setSuccess(false);
        response.setMessage("查询失败");

        CompletableFuture<StudentProfileResponseMessage> future = CompletableFuture.completedFuture(response);
        when(pendingStudentProfileService.create(anyString())).thenReturn(future);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.queryProfile(request));
        assertEquals("查询失败", ex.getMessage());
        verify(pendingStudentProfileService).remove(anyString());
    }

    @Test
    void sendAndWait_timeout_throwsFriendlyMessage() throws Exception {
        ReflectionTestUtils.setField(service, "timeoutSeconds", 1L);

        StudentProfileRequest request = org.mockito.Mockito.mock(StudentProfileRequest.class);
        when(request.getStudentId()).thenReturn("s001");

        @SuppressWarnings("unchecked")
        CompletableFuture<StudentProfileResponseMessage> future = org.mockito.Mockito.mock(CompletableFuture.class);
        when(pendingStudentProfileService.create(anyString())).thenReturn(future);
        when(future.get(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.eq(TimeUnit.SECONDS)))
                .thenThrow(new TimeoutException("timeout"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.queryProfile(request));
        assertEquals("学生档案服务响应超时", ex.getMessage());
        verify(pendingStudentProfileService).remove(anyString());
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
