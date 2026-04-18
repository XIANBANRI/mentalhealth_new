package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.entity.Teacher;
import com.sl.mentalhealth.mapper.TeacherMapper;
import com.sl.mentalhealth.vo.TeacherProfileResponseVO;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocalTeacherProfileServiceTest {

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private LocalTeacherProfileService service;

    @Test
    void getTeacherProfile_success() {
        Teacher teacher = new Teacher();
        teacher.setAccount("t001");
        teacher.setTeacherName("李老师");
        teacher.setPhone("13800000000");
        teacher.setOfficeLocation("A101");
        teacher.setAvatarUrl("/avatar/teacher/a.png");

        when(teacherMapper.selectById("t001")).thenReturn(teacher);

        TeacherProfileResponseVO result = service.getTeacherProfile("  t001  ");

        verify(teacherMapper).selectById("t001");
        assertEquals("t001", readField(result, "teacherAccount"));
        assertEquals("李老师", readField(result, "teacherName"));
        assertEquals("13800000000", readField(result, "phone"));
        assertEquals("A101", readField(result, "officeLocation"));
        assertEquals("/avatar/teacher/a.png", readField(result, "avatarUrl"));
    }

    @Test
    void getTeacherProfile_blankAccount_throwsException() {
        RuntimeException ex =
            assertThrows(RuntimeException.class, () -> service.getTeacherProfile(" "));

        assertEquals("老师账号不能为空", ex.getMessage());
    }

    @Test
    void getTeacherProfile_notFound_throwsException() {
        when(teacherMapper.selectById("t001")).thenReturn(null);

        RuntimeException ex =
            assertThrows(RuntimeException.class, () -> service.getTeacherProfile("t001"));

        assertEquals("未查询到老师信息", ex.getMessage());
    }

    @Test
    void updateAvatar_success() {
        Teacher teacher = new Teacher();
        teacher.setAccount("t001");
        teacher.setTeacherName("李老师");
        teacher.setPhone("13800000000");
        teacher.setOfficeLocation("A101");
        teacher.setAvatarUrl("/old.png");

        when(teacherMapper.selectById("t001")).thenReturn(teacher);
        when(teacherMapper.updateById(teacher)).thenReturn(1);

        TeacherProfileResponseVO result = service.updateAvatar(" t001 ", " /new.png ");

        assertEquals("/new.png", readField(result, "avatarUrl"));

        ArgumentCaptor<Teacher> captor = ArgumentCaptor.forClass(Teacher.class);
        verify(teacherMapper).updateById(captor.capture());
        assertEquals("/new.png", captor.getValue().getAvatarUrl());
    }

    @Test
    void updateAvatar_blankAvatar_throwsException() {
        RuntimeException ex =
            assertThrows(RuntimeException.class, () -> service.updateAvatar("t001", " "));

        assertEquals("头像地址不能为空", ex.getMessage());
    }

    private Object readField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            throw new RuntimeException("读取字段失败: " + fieldName, e);
        }
    }
}