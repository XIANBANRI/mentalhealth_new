package com.sl.mentalhealth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sl.mentalhealth.entity.Student;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

  Page<Student> selectPageByClassNamesAndKeyword(
      Page<Student> page,
      @Param("classNames") List<String> classNames,
      @Param("keyword") String keyword
  );

  Student selectAccessibleStudent(
      @Param("studentId") String studentId,
      @Param("classNames") List<String> classNames
  );
}