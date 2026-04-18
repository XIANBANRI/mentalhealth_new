package com.sl.mentalhealth.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sl.mentalhealth.entity.StudentAssessmentRecord;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentAssessmentRecordMapper extends BaseMapper<StudentAssessmentRecord> {

  default List<StudentAssessmentRecord> findByStudentIdOrderBySubmittedAtDescIdDesc(
      String studentId) {
    return this.selectList(
        new LambdaQueryWrapper<StudentAssessmentRecord>()
            .eq(StudentAssessmentRecord::getStudentId, studentId)
            .orderByDesc(StudentAssessmentRecord::getSubmittedAt)
            .orderByDesc(StudentAssessmentRecord::getId)
    );
  }

  default List<StudentAssessmentRecord> findByStudentIdAndSemesterOrderBySubmittedAtDescIdDesc(
      String studentId, String semester) {
    return this.selectList(
        new LambdaQueryWrapper<StudentAssessmentRecord>()
            .eq(StudentAssessmentRecord::getStudentId, studentId)
            .eq(StudentAssessmentRecord::getSemester, semester)
            .orderByDesc(StudentAssessmentRecord::getSubmittedAt)
            .orderByDesc(StudentAssessmentRecord::getId)
    );
  }

  default List<StudentAssessmentRecord> findByStudentIdAndSemesterOrderBySubmittedAtAscIdAsc(
      String studentId, String semester) {
    return this.selectList(
        new LambdaQueryWrapper<StudentAssessmentRecord>()
            .eq(StudentAssessmentRecord::getStudentId, studentId)
            .eq(StudentAssessmentRecord::getSemester, semester)
            .orderByAsc(StudentAssessmentRecord::getSubmittedAt)
            .orderByAsc(StudentAssessmentRecord::getId)
    );
  }

  default Optional<StudentAssessmentRecord> findFirstByStudentIdAndSemesterAndScaleId(
      String studentId, String semester, Long scaleId) {
    List<StudentAssessmentRecord> list = this.selectList(
        new LambdaQueryWrapper<StudentAssessmentRecord>()
            .eq(StudentAssessmentRecord::getStudentId, studentId)
            .eq(StudentAssessmentRecord::getSemester, semester)
            .eq(StudentAssessmentRecord::getScaleId, scaleId)
            .last("LIMIT 1")
    );
    return list.stream().findFirst();
  }

  default StudentAssessmentRecord saveRecord(StudentAssessmentRecord record) {
    if (record.getId() == null) {
      this.insert(record);
    } else {
      this.updateById(record);
    }
    return record;
  }
}