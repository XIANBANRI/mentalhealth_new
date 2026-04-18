package com.sl.mentalhealth.mapper;

import com.sl.mentalhealth.entity.Student;
import com.sl.mentalhealth.entity.StudentAssessmentSemesterSummary;
import com.sl.mentalhealth.mapper.result.ClassDangerCountResult;
import com.sl.mentalhealth.mapper.result.SemesterDangerCountResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentAssessmentSemesterSummaryMapper {

  StudentAssessmentSemesterSummary selectByStudentIdAndSemester(@Param("studentId") String studentId,
      @Param("semester") String semester);

  List<StudentAssessmentSemesterSummary> selectByStudentIdOrderByLastTestedAtDescIdDesc(@Param("studentId") String studentId);

  Integer countByStudentIdAndSemesterAndSemesterLevel(@Param("studentId") String studentId,
      @Param("semester") String semester,
      @Param("semesterLevel") String semesterLevel);

  List<Student> selectDangerousStudentsBySemesterAndClassNames(@Param("semester") String semester,
      @Param("classNames") List<String> classNames,
      @Param("offset") long offset,
      @Param("pageSize") int pageSize);

  Long countDangerousStudentsBySemesterAndClassNames(@Param("semester") String semester,
      @Param("classNames") List<String> classNames);

  List<ClassDangerCountResult> selectDangerCountByClass(@Param("counselorAccount") String counselorAccount,
      @Param("semester") String semester);

  List<SemesterDangerCountResult> selectDangerCountBySemester(@Param("counselorAccount") String counselorAccount);

  int insertSummary(StudentAssessmentSemesterSummary summary);

  int updateSummaryById(StudentAssessmentSemesterSummary summary);
}