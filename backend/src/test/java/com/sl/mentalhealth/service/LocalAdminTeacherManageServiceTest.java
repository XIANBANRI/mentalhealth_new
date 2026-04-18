package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sl.mentalhealth.dto.AdminTeacherCreateRequest;
import com.sl.mentalhealth.dto.AdminTeacherQueryRequest;
import com.sl.mentalhealth.dto.AdminTeacherUpdateRequest;
import com.sl.mentalhealth.entity.Teacher;
import com.sl.mentalhealth.kafka.message.AdminTeacherManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminTeacherManageResponseMessage;
import com.sl.mentalhealth.mapper.TeacherMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocalAdminTeacherManageServiceTest {

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private LocalAdminTeacherManageService service;

    @Test
    void handle_queryPage_success() {
        Teacher teacher = new Teacher();
        teacher.setAccount("t001");
        teacher.setTeacherName("李老师");
        teacher.setOfficeLocation("A101");
        teacher.setPhone("13800000000");

        Page<Teacher> teacherPage = new Page<>(1, 10);
        teacherPage.setRecords(List.of(teacher));
        teacherPage.setTotal(1L);

        when(teacherMapper.selectPageByCondition(
            any(Page.class), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(teacherPage);

        AdminTeacherQueryRequest queryRequest = new AdminTeacherQueryRequest();
        queryRequest.setPageNum(1);
        queryRequest.setPageSize(10);
        queryRequest.setAccount("t");
        queryRequest.setTeacherName("李");
        queryRequest.setOfficeLocation("A");
        queryRequest.setPhone("138");

        AdminTeacherManageRequestMessage requestMessage = new AdminTeacherManageRequestMessage();
        requestMessage.setRequestId("r1");
        requestMessage.setAction(AdminTeacherManageRequestMessage.ACTION_QUERY_PAGE);
        requestMessage.setQueryRequest(queryRequest);

        AdminTeacherManageResponseMessage result = service.handle(requestMessage);

        assertTrue(result.isSuccess());
        assertEquals("查询成功", result.getMessage());
        assertEquals(1L, result.getPage().getTotal());
        assertEquals(1, result.getPage().getList().size());
        assertEquals("t001", result.getPage().getList().get(0).getAccount());
    }

    @Test
    void handle_detail_success() {
        Teacher teacher = new Teacher();
        teacher.setAccount("t001");
        teacher.setTeacherName("李老师");
        teacher.setOfficeLocation("A101");
        teacher.setPhone("13800000000");
        teacher.setAvatarUrl("/files/avatar/teacher/a.png");

        when(teacherMapper.selectById("t001")).thenReturn(teacher);

        AdminTeacherManageRequestMessage requestMessage = new AdminTeacherManageRequestMessage();
        requestMessage.setRequestId("r2");
        requestMessage.setAction(AdminTeacherManageRequestMessage.ACTION_DETAIL);
        requestMessage.setAccount("t001");

        AdminTeacherManageResponseMessage result = service.handle(requestMessage);

        assertTrue(result.isSuccess());
        assertEquals("查询成功", result.getMessage());
        assertEquals("t001", result.getTeacher().getAccount());
        assertEquals("李老师", result.getTeacher().getTeacherName());
    }

    @Test
    void handle_create_success() {
        when(teacherMapper.selectById("t001")).thenReturn(null);
        when(teacherMapper.insert(any(Teacher.class))).thenReturn(1);

        AdminTeacherCreateRequest createRequest = new AdminTeacherCreateRequest();
        createRequest.setAccount("t001");
        createRequest.setPassword("123456");
        createRequest.setTeacherName("李老师");
        createRequest.setOfficeLocation("A101");
        createRequest.setPhone("13800000000");

        AdminTeacherManageRequestMessage requestMessage = new AdminTeacherManageRequestMessage();
        requestMessage.setRequestId("r3");
        requestMessage.setAction(AdminTeacherManageRequestMessage.ACTION_CREATE);
        requestMessage.setCreateRequest(createRequest);

        AdminTeacherManageResponseMessage result = service.handle(requestMessage);

        assertTrue(result.isSuccess());
        assertEquals("新增成功", result.getMessage());
        assertEquals("t001", result.getTeacher().getAccount());
        assertEquals("李老师", result.getTeacher().getTeacherName());
    }

    @Test
    void handle_create_duplicateAccount_returnsFailResponse() {
        Teacher teacher = new Teacher();
        teacher.setAccount("t001");
        when(teacherMapper.selectById("t001")).thenReturn(teacher);

        AdminTeacherCreateRequest createRequest = new AdminTeacherCreateRequest();
        createRequest.setAccount("t001");
        createRequest.setPassword("123456");

        AdminTeacherManageRequestMessage requestMessage = new AdminTeacherManageRequestMessage();
        requestMessage.setRequestId("r4");
        requestMessage.setAction(AdminTeacherManageRequestMessage.ACTION_CREATE);
        requestMessage.setCreateRequest(createRequest);

        AdminTeacherManageResponseMessage result = service.handle(requestMessage);

        assertFalse(result.isSuccess());
        assertEquals("老师账号已存在", result.getMessage());
    }

    @Test
    void handle_update_success() {
        Teacher teacher = new Teacher();
        teacher.setAccount("t001");
        teacher.setPassword("old");

        when(teacherMapper.selectById("t001")).thenReturn(teacher);
        when(teacherMapper.updateById(teacher)).thenReturn(1);

        AdminTeacherUpdateRequest updateRequest = new AdminTeacherUpdateRequest();
        updateRequest.setAccount("t001");
        updateRequest.setPassword("new123");
        updateRequest.setTeacherName("新老师");
        updateRequest.setOfficeLocation("B201");
        updateRequest.setPhone("13900000000");
        updateRequest.setAvatarUrl("/files/avatar/teacher/new.png");

        AdminTeacherManageRequestMessage requestMessage = new AdminTeacherManageRequestMessage();
        requestMessage.setRequestId("r5");
        requestMessage.setAction(AdminTeacherManageRequestMessage.ACTION_UPDATE);
        requestMessage.setUpdateRequest(updateRequest);

        AdminTeacherManageResponseMessage result = service.handle(requestMessage);

        assertTrue(result.isSuccess());
        assertEquals("修改成功", result.getMessage());
        assertEquals("t001", result.getTeacher().getAccount());
        assertEquals("新老师", result.getTeacher().getTeacherName());
        assertEquals("B201", result.getTeacher().getOfficeLocation());
        assertEquals("13900000000", result.getTeacher().getPhone());
        assertEquals("/files/avatar/teacher/new.png", result.getTeacher().getAvatarUrl());
    }

    @Test
    void handle_invalidAction_returnsFailResponse() {
        AdminTeacherManageRequestMessage requestMessage = new AdminTeacherManageRequestMessage();
        requestMessage.setRequestId("r6");
        requestMessage.setAction("UNKNOWN");

        AdminTeacherManageResponseMessage result = service.handle(requestMessage);

        assertFalse(result.isSuccess());
        assertEquals("不支持的操作类型", result.getMessage());
    }
}