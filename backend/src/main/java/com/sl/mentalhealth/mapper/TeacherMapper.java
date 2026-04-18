package com.sl.mentalhealth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sl.mentalhealth.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

  Page<Teacher> selectPageByCondition(
      Page<Teacher> page,
      @Param("account") String account,
      @Param("teacherName") String teacherName,
      @Param("officeLocation") String officeLocation,
      @Param("phone") String phone
  );
}