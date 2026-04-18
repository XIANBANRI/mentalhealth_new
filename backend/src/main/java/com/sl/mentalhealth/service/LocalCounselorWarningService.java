package com.sl.mentalhealth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sl.mentalhealth.entity.CounselorClassMapping;
import com.sl.mentalhealth.entity.Student;
import com.sl.mentalhealth.entity.StudentAssessmentRecord;
import com.sl.mentalhealth.mapper.CounselorClassMappingMapper;
import com.sl.mentalhealth.mapper.StudentAssessmentRecordMapper;
import com.sl.mentalhealth.mapper.StudentAssessmentSemesterSummaryMapper;
import com.sl.mentalhealth.mapper.StudentMapper;
import com.sl.mentalhealth.vo.CounselorWarningDetailVO;
import com.sl.mentalhealth.vo.CounselorWarningPageVO;
import com.sl.mentalhealth.vo.CounselorWarningRecordVO;
import com.sl.mentalhealth.vo.CounselorWarningStudentVO;
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
public class LocalCounselorWarningService {

  private final CounselorClassMappingMapper counselorClassMappingMapper;
  private final StudentAssessmentSemesterSummaryMapper studentAssessmentSemesterSummaryMapper;
  private final StudentAssessmentRecordMapper studentAssessmentRecordMapper;
  private final StudentMapper studentMapper;

  public List<String> listManagedClasses(String counselorAccount) {
    validateCounselorAccount(counselorAccount);
    return getManagedClasses(counselorAccount);
  }

  public CounselorWarningPageVO listDangerousStudents(String counselorAccount,
      String semester,
      String className,
      Integer pageNum,
      Integer pageSize) {
    validateCounselorAccount(counselorAccount);

    String safeSemester = normalizeSemester(semester);
    List<String> targetClasses = resolveTargetClasses(counselorAccount, className);
    if (targetClasses.isEmpty()) {
      return CounselorWarningPageVO.builder()
          .list(Collections.emptyList())
          .total(0L)
          .build();
    }

    int safePageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;
    int safePageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;
    long offset = (long) (safePageNum - 1) * safePageSize;

    List<Student> students = selectDangerousStudents(
        safeSemester,
        targetClasses,
        offset,
        safePageSize
    );

    Long total = countDangerousStudents(safeSemester, targetClasses);

    List<CounselorWarningStudentVO> list = students.stream()
        .map(this::toWarningStudentVO)
        .collect(Collectors.toList());

    return CounselorWarningPageVO.builder()
        .list(list)
        .total(total == null ? 0L : total)
        .build();
  }

  public List<CounselorWarningStudentVO> exportDangerousStudents(String counselorAccount,
      String semester,
      String className) {
    validateCounselorAccount(counselorAccount);

    String safeSemester = normalizeSemester(semester);
    List<String> targetClasses = resolveTargetClasses(counselorAccount, className);
    if (targetClasses.isEmpty()) {
      return Collections.emptyList();
    }

    Long total = countDangerousStudents(safeSemester, targetClasses);
    if (total == null || total <= 0) {
      return Collections.emptyList();
    }

    int fetchSize = total > Integer.MAX_VALUE ? Integer.MAX_VALUE : total.intValue();

    List<Student> students = selectDangerousStudents(
        safeSemester,
        targetClasses,
        0L,
        fetchSize
    );

    return students.stream()
        .map(this::toWarningStudentVO)
        .collect(Collectors.toList());
  }

  public CounselorWarningDetailVO getDangerousStudentDetail(String counselorAccount,
      String studentId,
      String semester) {
    validateCounselorAccount(counselorAccount);

    if (!StringUtils.hasText(studentId)) {
      throw new IllegalArgumentException("学生学号不能为空");
    }

    String safeSemester = normalizeSemester(semester);
    List<String> managedClasses = getManagedClasses(counselorAccount);
    if (managedClasses.isEmpty()) {
      throw new IllegalArgumentException("当前辅导员未绑定任何班级");
    }

    Student student = studentMapper.selectById(studentId.trim());
    if (student == null) {
      throw new IllegalArgumentException("学生不存在");
    }

    if (!managedClasses.contains(student.getClassName())) {
      throw new IllegalArgumentException("无权查看该学生预警详情");
    }

    Integer dangerousCount = studentAssessmentSemesterSummaryMapper
        .countByStudentIdAndSemesterAndSemesterLevel(
            student.getStudentId(),
            safeSemester,
            "危险"
        );

    boolean dangerous = dangerousCount != null && dangerousCount > 0;

    if (!dangerous) {
      throw new IllegalArgumentException("该学生在当前学期不属于危险预警名单");
    }

    List<CounselorWarningRecordVO> records = studentAssessmentRecordMapper
        .findByStudentIdAndSemesterOrderBySubmittedAtDescIdDesc(
            student.getStudentId(),
            safeSemester
        )
        .stream()
        .map(this::toRecordVO)
        .collect(Collectors.toList());

    return CounselorWarningDetailVO.builder()
        .studentId(student.getStudentId())
        .name(student.getName())
        .className(student.getClassName())
        .college(student.getCollege())
        .phone(student.getPhone())
        .semester(safeSemester)
        .records(records)
        .build();
  }

  private String normalizeSemester(String semester) {
    return StringUtils.hasText(semester) ? semester.trim() : "第1学期";
  }

  private List<String> resolveTargetClasses(String counselorAccount, String className) {
    List<String> managedClasses = getManagedClasses(counselorAccount);
    if (managedClasses.isEmpty()) {
      return Collections.emptyList();
    }

    if (!StringUtils.hasText(className)) {
      return managedClasses;
    }

    String selectedClass = className.trim();
    if (!managedClasses.contains(selectedClass)) {
      throw new IllegalArgumentException("无权查看该班级预警信息");
    }

    return Collections.singletonList(selectedClass);
  }

  private List<Student> selectDangerousStudents(String semester,
      List<String> targetClasses,
      long offset,
      int pageSize) {
    return studentAssessmentSemesterSummaryMapper
        .selectDangerousStudentsBySemesterAndClassNames(
            semester,
            targetClasses,
            offset,
            pageSize
        );
  }

  private Long countDangerousStudents(String semester, List<String> targetClasses) {
    Long total = studentAssessmentSemesterSummaryMapper
        .countDangerousStudentsBySemesterAndClassNames(semester, targetClasses);
    return total == null ? 0L : total;
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

  private CounselorWarningStudentVO toWarningStudentVO(Student student) {
    return CounselorWarningStudentVO.builder()
        .studentId(student.getStudentId())
        .name(student.getName())
        .className(student.getClassName())
        .college(student.getCollege())
        .phone(student.getPhone())
        .build();
  }

  private CounselorWarningRecordVO toRecordVO(StudentAssessmentRecord record) {
    return CounselorWarningRecordVO.builder()
        .id(record.getId())
        .scaleCode(record.getScaleCode())
        .scaleName(record.getScaleName())
        .rawScore(record.getRawScore())
        .resultLevel(record.getResultLevel())
        .resultSummary(record.getResultSummary())
        .suggestion(record.getSuggestion())
        .submittedAt(record.getSubmittedAt())
        .build();
  }

  private void validateCounselorAccount(String counselorAccount) {
    if (!StringUtils.hasText(counselorAccount)) {
      throw new IllegalArgumentException("辅导员账号不能为空");
    }
  }
}