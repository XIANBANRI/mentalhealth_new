package com.sl.mentalhealth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sl.mentalhealth.entity.TeacherSchedule;
import java.time.LocalTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TeacherScheduleMapper extends BaseMapper<TeacherSchedule> {

  List<TeacherSchedule> selectByWeekDayAndStatusOrderByStartTimeAsc(
      @Param("weekDay") Integer weekDay,
      @Param("status") Integer status
  );

  List<TeacherSchedule> selectByTeacherAccountAndStatusOrderByWeekDayAscStartTimeAsc(
      @Param("teacherAccount") String teacherAccount,
      @Param("status") Integer status
  );

  List<TeacherSchedule> selectByTeacherAccountAndWeekDayAndStatusOrderByStartTimeAsc(
      @Param("teacherAccount") String teacherAccount,
      @Param("weekDay") Integer weekDay,
      @Param("status") Integer status
  );

  TeacherSchedule selectByIdAndTeacherAccount(
      @Param("id") Long id,
      @Param("teacherAccount") String teacherAccount
  );

  Long countByTeacherAccountAndWeekDayAndStartTimeAndEndTimeAndStatus(
      @Param("teacherAccount") String teacherAccount,
      @Param("weekDay") Integer weekDay,
      @Param("startTime") LocalTime startTime,
      @Param("endTime") LocalTime endTime,
      @Param("status") Integer status
  );

  Long countByTeacherAccountAndWeekDayAndStartTimeAndEndTimeAndStatusAndIdNot(
      @Param("teacherAccount") String teacherAccount,
      @Param("weekDay") Integer weekDay,
      @Param("startTime") LocalTime startTime,
      @Param("endTime") LocalTime endTime,
      @Param("status") Integer status,
      @Param("id") Long id
  );

  TeacherSchedule selectByTeacherAccountAndWeekDayAndStartTimeAndEndTimeAndStatus(
      @Param("teacherAccount") String teacherAccount,
      @Param("weekDay") Integer weekDay,
      @Param("startTime") LocalTime startTime,
      @Param("endTime") LocalTime endTime,
      @Param("status") Integer status
  );
}