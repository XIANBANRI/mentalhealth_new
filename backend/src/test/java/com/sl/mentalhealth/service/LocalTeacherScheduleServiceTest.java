package com.sl.mentalhealth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sl.mentalhealth.dto.TeacherScheduleDeleteRequest;
import com.sl.mentalhealth.dto.TeacherScheduleQueryRequest;
import com.sl.mentalhealth.dto.TeacherScheduleSaveRequest;
import com.sl.mentalhealth.entity.Teacher;
import com.sl.mentalhealth.entity.TeacherSchedule;
import com.sl.mentalhealth.mapper.AppointmentMapper;
import com.sl.mentalhealth.mapper.TeacherMapper;
import com.sl.mentalhealth.mapper.TeacherScheduleMapper;
import com.sl.mentalhealth.vo.TeacherScheduleVO;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocalTeacherScheduleServiceTest {

    @Mock
    private TeacherScheduleMapper teacherScheduleMapper;

    @Mock
    private TeacherMapper teacherMapper;

    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private LocalTeacherScheduleService service;

    private void mockTeacherExists(String account) {
        Teacher teacher = new Teacher();
        teacher.setAccount(account);
        when(teacherMapper.selectById(account)).thenReturn(teacher);
    }

    @Test
    void query_allEnabled_success() {
        TeacherScheduleQueryRequest request = org.mockito.Mockito.mock(TeacherScheduleQueryRequest.class);
        when(request.getTeacherAccount()).thenReturn("t001");
        when(request.getWeekDay()).thenReturn(null);
        mockTeacherExists("t001");

        TeacherSchedule schedule = new TeacherSchedule();
        schedule.setId(1L);
        schedule.setTeacherAccount("t001");
        schedule.setWeekDay(1);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(10, 0));
        schedule.setMaxAppointments(3);
        schedule.setRemark("上午");

        when(teacherScheduleMapper.selectByTeacherAccountAndStatusOrderByWeekDayAscStartTimeAsc("t001", 1))
            .thenReturn(List.of(schedule));

        List<TeacherScheduleVO> result = service.query(request);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("t001", result.get(0).getTeacherAccount());
        assertEquals("09:00", result.get(0).getStartTime());
        assertEquals("10:00", result.get(0).getEndTime());
    }

    @Test
    void add_newRecord_success() {
        TeacherScheduleSaveRequest request = org.mockito.Mockito.mock(TeacherScheduleSaveRequest.class);
        when(request.getTeacherAccount()).thenReturn("t001");
        when(request.getWeekDay()).thenReturn(1);
        when(request.getStartTime()).thenReturn("09:00");
        when(request.getEndTime()).thenReturn("10:00");
        when(request.getMaxAppointments()).thenReturn(3);
        when(request.getRemark()).thenReturn("上午");
        mockTeacherExists("t001");

        when(teacherScheduleMapper.countByTeacherAccountAndWeekDayAndStartTimeAndEndTimeAndStatus(
            "t001", 1, LocalTime.of(9, 0), LocalTime.of(10, 0), 1)).thenReturn(0L);

        when(teacherScheduleMapper.selectByTeacherAccountAndWeekDayAndStartTimeAndEndTimeAndStatus(
            "t001", 1, LocalTime.of(9, 0), LocalTime.of(10, 0), 0)).thenReturn(null);

        when(teacherScheduleMapper.insert(any(TeacherSchedule.class))).thenAnswer(invocation -> {
            TeacherSchedule entity = invocation.getArgument(0);
            entity.setId(1L);
            return 1;
        });

        TeacherScheduleVO result = service.add(request);

        assertEquals(1L, result.getId());
        assertEquals(1, result.getWeekDay());
        assertEquals(3, result.getMaxAppointments());
        assertEquals("上午", result.getRemark());
        assertEquals("09:00", result.getStartTime());
    }

    @Test
    void add_duplicateEnabledRecord_throwsException() {
        TeacherScheduleSaveRequest request = org.mockito.Mockito.mock(TeacherScheduleSaveRequest.class);
        when(request.getTeacherAccount()).thenReturn("t001");
        when(request.getWeekDay()).thenReturn(1);
        when(request.getStartTime()).thenReturn("09:00");
        when(request.getEndTime()).thenReturn("10:00");
        when(request.getMaxAppointments()).thenReturn(3);
        mockTeacherExists("t001");

        when(teacherScheduleMapper.countByTeacherAccountAndWeekDayAndStartTimeAndEndTimeAndStatus(
            "t001", 1, LocalTime.of(9, 0), LocalTime.of(10, 0), 1)).thenReturn(1L);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.add(request));
        assertEquals("该工作时间已存在，请勿重复添加", ex.getMessage());
    }

    @Test
    void update_missingId_throwsException() {
        TeacherScheduleSaveRequest request = org.mockito.Mockito.mock(TeacherScheduleSaveRequest.class);
        when(request.getTeacherAccount()).thenReturn("t001");
        when(request.getWeekDay()).thenReturn(1);
        when(request.getStartTime()).thenReturn("09:00");
        when(request.getEndTime()).thenReturn("10:00");
        when(request.getMaxAppointments()).thenReturn(3);
        when(request.getId()).thenReturn(null);
        mockTeacherExists("t001");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.update(request));
        assertEquals("工作时间ID不能为空", ex.getMessage());
    }

    @Test
    void delete_hasActiveAppointments_throwsException() {
        TeacherScheduleDeleteRequest request = org.mockito.Mockito.mock(TeacherScheduleDeleteRequest.class);
        when(request.getId()).thenReturn(1L);
        when(request.getTeacherAccount()).thenReturn("t001");
        mockTeacherExists("t001");

        TeacherSchedule entity = new TeacherSchedule();
        entity.setId(1L);
        entity.setTeacherAccount("t001");

        when(teacherScheduleMapper.selectByIdAndTeacherAccount(1L, "t001")).thenReturn(entity);
        when(appointmentMapper.selectCount(any())).thenReturn(2L);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.delete(request));
        assertEquals("该工作时间存在未完成的预约记录，无法删除。只有已完成、已拒绝或已取消的预约全部处理完后才可删除。", ex.getMessage());
    }

    @Test
    void delete_success_setsDisabledStatus() {
        TeacherScheduleDeleteRequest request = org.mockito.Mockito.mock(TeacherScheduleDeleteRequest.class);
        when(request.getId()).thenReturn(1L);
        when(request.getTeacherAccount()).thenReturn("t001");
        mockTeacherExists("t001");

        TeacherSchedule entity = new TeacherSchedule();
        entity.setId(1L);
        entity.setTeacherAccount("t001");
        entity.setStatus(1);

        when(teacherScheduleMapper.selectByIdAndTeacherAccount(1L, "t001")).thenReturn(entity);
        when(appointmentMapper.selectCount(any())).thenReturn(0L);
        when(teacherScheduleMapper.updateById(any(TeacherSchedule.class))).thenReturn(1);

        service.delete(request);

        ArgumentCaptor<TeacherSchedule> captor = ArgumentCaptor.forClass(TeacherSchedule.class);
        verify(teacherScheduleMapper).updateById(captor.capture());
        assertEquals(0, captor.getValue().getStatus());
    }

    @Test
    void query_invalidWeekDay_throwsException() {
        TeacherScheduleQueryRequest request = org.mockito.Mockito.mock(TeacherScheduleQueryRequest.class);
        when(request.getTeacherAccount()).thenReturn("t001");
        when(request.getWeekDay()).thenReturn(8);
        mockTeacherExists("t001");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.query(request));
        assertEquals("星期参数不合法", ex.getMessage());
    }

    @Test
    void add_startTimeAfterEndTime_throwsException() {
        TeacherScheduleSaveRequest request = org.mockito.Mockito.mock(TeacherScheduleSaveRequest.class);
        when(request.getTeacherAccount()).thenReturn("t001");
        when(request.getWeekDay()).thenReturn(1);
        when(request.getStartTime()).thenReturn("10:00");
        when(request.getEndTime()).thenReturn("09:00");
        when(request.getMaxAppointments()).thenReturn(3);
        mockTeacherExists("t001");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.add(request));
        assertEquals("开始时间必须早于结束时间", ex.getMessage());
    }
}