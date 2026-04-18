package com.sl.mentalhealth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sl.mentalhealth.entity.CounselorClassMapping;
import com.sl.mentalhealth.entity.Student;
import com.sl.mentalhealth.entity.StudentAssessmentSemesterSummary;
import com.sl.mentalhealth.mapper.CounselorClassMappingMapper;
import com.sl.mentalhealth.mapper.StudentAssessmentSemesterSummaryMapper;
import com.sl.mentalhealth.mapper.StudentMapper;
import com.sl.mentalhealth.vo.CounselorStudentAssessmentSummaryVO;
import com.sl.mentalhealth.vo.CounselorStudentDetailVO;
import com.sl.mentalhealth.vo.CounselorStudentPageVO;
import com.sl.mentalhealth.vo.CounselorStudentVO;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocalCounselorStudentService {

  private final CounselorClassMappingMapper counselorClassMappingMapper;
  private final StudentMapper studentMapper;
  private final StudentAssessmentSemesterSummaryMapper studentAssessmentSemesterSummaryMapper;

  public List<String> listManagedClasses(String counselorAccount) {
    validateCounselorAccount(counselorAccount);
    return getManagedClasses(counselorAccount);
  }

  public CounselorStudentPageVO listStudents(String counselorAccount, String className, String keyword,
      Integer pageNum, Integer pageSize) {
    validateCounselorAccount(counselorAccount);

    List<String> managedClasses = getManagedClasses(counselorAccount);
    if (managedClasses.isEmpty()) {
      return CounselorStudentPageVO.builder()
          .list(Collections.emptyList())
          .total(0L)
          .build();
    }

    List<String> targetClasses = managedClasses;
    if (StringUtils.hasText(className) && !"全部".equals(className.trim())) {
      String selectedClassName = className.trim();
      if (!managedClasses.contains(selectedClassName)) {
        throw new IllegalArgumentException("无权查看该班级学生");
      }
      targetClasses = Collections.singletonList(selectedClassName);
    }

    int safePageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;
    int safePageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;

    Page<Student> page = new Page<>(safePageNum, safePageSize);
    Page<Student> studentPage = studentMapper.selectPageByClassNamesAndKeyword(
        page,
        targetClasses,
        StringUtils.hasText(keyword) ? keyword.trim() : null
    );

    List<CounselorStudentVO> list = studentPage.getRecords()
        .stream()
        .map(this::toStudentVO)
        .collect(Collectors.toList());

    return CounselorStudentPageVO.builder()
        .list(list)
        .total(studentPage.getTotal())
        .build();
  }

  public CounselorStudentDetailVO getStudentDetail(String counselorAccount, String studentId) {
    validateCounselorAccount(counselorAccount);

    if (!StringUtils.hasText(studentId)) {
      throw new IllegalArgumentException("学生学号不能为空");
    }

    List<String> managedClasses = getManagedClasses(counselorAccount);
    if (managedClasses.isEmpty()) {
      throw new IllegalArgumentException("当前辅导员未绑定任何班级");
    }

    Student student = studentMapper.selectAccessibleStudent(studentId.trim(), managedClasses);
    if (student == null) {
      throw new IllegalArgumentException("无权查看该学生信息，或学生不存在");
    }

    List<CounselorStudentAssessmentSummaryVO> summaries =
        studentAssessmentSemesterSummaryMapper
            .selectByStudentIdOrderByLastTestedAtDescIdDesc(student.getStudentId())
            .stream()
            .map(this::toAssessmentSummaryVO)
            .collect(Collectors.toList());

    return CounselorStudentDetailVO.builder()
        .studentId(student.getStudentId())
        .name(student.getName())
        .college(student.getCollege())
        .className(student.getClassName())
        .grade(student.getGrade())
        .phone(student.getPhone())
        .assessmentSummaries(summaries)
        .build();
  }

  private List<String> getManagedClasses(String counselorAccount) {
    return counselorClassMappingMapper.selectList(
            Wrappers.<CounselorClassMapping>lambdaQuery()
                .eq(CounselorClassMapping::getCounselorAccount, counselorAccount)
                .orderByAsc(CounselorClassMapping::getClassName)
        )
        .stream()
        .map(CounselorClassMapping::getClassName)
        .filter(StringUtils::hasText)
        .distinct()
        .collect(Collectors.toList());
  }

  private CounselorStudentVO toStudentVO(Student student) {
    return CounselorStudentVO.builder()
        .studentId(student.getStudentId())
        .name(student.getName())
        .college(student.getCollege())
        .className(student.getClassName())
        .grade(student.getGrade())
        .phone(student.getPhone())
        .build();
  }

  private CounselorStudentAssessmentSummaryVO toAssessmentSummaryVO(
      StudentAssessmentSemesterSummary summary) {
    return CounselorStudentAssessmentSummaryVO.builder()
        .id(summary.getId())
        .studentId(summary.getStudentId())
        .semester(summary.getSemester())
        .testedCount(summary.getTestedCount())
        .scoreSummary(summary.getScoreSummary())
        .semesterLevel(summary.getSemesterLevel())
        .lastTestedAt(summary.getLastTestedAt())
        .createdAt(summary.getCreatedAt())
        .updatedAt(summary.getUpdatedAt())
        .build();
  }

  private void validateCounselorAccount(String counselorAccount) {
    if (!StringUtils.hasText(counselorAccount)) {
      throw new IllegalArgumentException("辅导员账号不能为空");
    }
  }
}