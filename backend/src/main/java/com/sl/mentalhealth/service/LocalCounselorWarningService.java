package com.sl.mentalhealth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocalCounselorWarningService {

  /**
   * 辅导员管理班级缓存时间。
   */
  private static final long MANAGED_CLASSES_CACHE_TTL_HOURS = 24L;

  /**
   * 危险学生列表缓存时间。
   * 数据只在测评提交后变化，不需要每次实时 JOIN 查询。
   */
  private static final long DANGEROUS_STUDENTS_CACHE_TTL_MINUTES = 15L;

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final CounselorClassMappingMapper counselorClassMappingMapper;
  private final StudentAssessmentSemesterSummaryMapper studentAssessmentSemesterSummaryMapper;
  private final StudentAssessmentRecordMapper studentAssessmentRecordMapper;
  private final StudentMapper studentMapper;
  private final StringRedisTemplate stringRedisTemplate;

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

    List<CounselorWarningStudentVO> allDangerousStudents =
        getDangerousStudentsFromCacheOrDb(
            counselorAccount,
            safeSemester,
            className,
            targetClasses
        );

    List<CounselorWarningStudentVO> pageList =
        paginateWarningStudents(allDangerousStudents, safePageNum, safePageSize);

    return CounselorWarningPageVO.builder()
        .list(pageList)
        .total((long) allDangerousStudents.size())
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

    return getDangerousStudentsFromCacheOrDb(
        counselorAccount,
        safeSemester,
        className,
        targetClasses
    );
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

  /**
   * 管理员修改辅导员-班级绑定后，可以调用这个方法清理该辅导员相关缓存。
   * 会同时清理：
   * 1. counselor:classes:{account}
   * 2. warning:dangerous:{account}:*
   */
  public void evictCounselorWarningCache(String counselorAccount) {
    if (!StringUtils.hasText(counselorAccount)) {
      return;
    }

    String account = counselorAccount.trim();

    stringRedisTemplate.delete(buildManagedClassesCacheKey(account));

    Set<String> dangerousKeys = stringRedisTemplate.keys(
        "warning:dangerous:" + normalizeKeyPart(account) + ":*"
    );

    if (dangerousKeys != null && !dangerousKeys.isEmpty()) {
      stringRedisTemplate.delete(dangerousKeys);
    }
  }

  /**
   * 测评提交后，如果需要立即刷新辅导员预警列表，可以调用这个方法。
   */
  public void evictDangerousStudentsCache(String counselorAccount) {
    if (!StringUtils.hasText(counselorAccount)) {
      return;
    }

    Set<String> dangerousKeys = stringRedisTemplate.keys(
        "warning:dangerous:" + normalizeKeyPart(counselorAccount) + ":*"
    );

    if (dangerousKeys != null && !dangerousKeys.isEmpty()) {
      stringRedisTemplate.delete(dangerousKeys);
    }
  }

  private List<CounselorWarningStudentVO> getDangerousStudentsFromCacheOrDb(
      String counselorAccount,
      String semester,
      String className,
      List<String> targetClasses) {

    String cacheKey = buildDangerousStudentsCacheKey(counselorAccount, semester, className);

    List<CounselorWarningStudentVO> cachedList = readDangerousStudentsCache(cacheKey);
    if (cachedList != null) {
      return cachedList;
    }

    Long total = countDangerousStudents(semester, targetClasses);
    if (total <= 0) {
      writeJsonCache(cacheKey, Collections.emptyList(),
          DANGEROUS_STUDENTS_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
      return Collections.emptyList();
    }

    int fetchSize = total > Integer.MAX_VALUE ? Integer.MAX_VALUE : total.intValue();

    List<Student> students = selectDangerousStudents(
        semester,
        targetClasses,
        0L,
        fetchSize
    );

    List<CounselorWarningStudentVO> list = students.stream()
        .map(this::toWarningStudentVO)
        .collect(Collectors.toList());

    writeDangerousStudentsCache(cacheKey, students);

    return list;
  }

  private List<CounselorWarningStudentVO> paginateWarningStudents(
      List<CounselorWarningStudentVO> allList,
      int pageNum,
      int pageSize) {

    if (allList == null || allList.isEmpty()) {
      return Collections.emptyList();
    }

    int fromIndex = (int) Math.min((long) (pageNum - 1) * pageSize, allList.size());
    int toIndex = Math.min(fromIndex + pageSize, allList.size());

    if (fromIndex >= toIndex) {
      return Collections.emptyList();
    }

    return new ArrayList<>(allList.subList(fromIndex, toIndex));
  }

  private String normalizeSemester(String semester) {
    return StringUtils.hasText(semester) ? semester.trim() : "第1学期";
  }

  private List<String> resolveTargetClasses(String counselorAccount, String className) {
    List<String> managedClasses = getManagedClasses(counselorAccount);
    if (managedClasses.isEmpty()) {
      return Collections.emptyList();
    }

    if (!StringUtils.hasText(className) || "全部".equals(className.trim())) {
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
    String cacheKey = buildManagedClassesCacheKey(counselorAccount);

    List<String> cachedClasses = readStringListCache(cacheKey);
    if (cachedClasses != null) {
      return cachedClasses;
    }

    List<String> classes = counselorClassMappingMapper.selectList(
            Wrappers.<CounselorClassMapping>lambdaQuery()
                .eq(CounselorClassMapping::getCounselorAccount, counselorAccount)
                .orderByAsc(CounselorClassMapping::getClassName)
        )
        .stream()
        .map(CounselorClassMapping::getClassName)
        .filter(StringUtils::hasText)
        .distinct()
        .collect(Collectors.toList());

    writeJsonCache(cacheKey, classes, MANAGED_CLASSES_CACHE_TTL_HOURS, TimeUnit.HOURS);

    return classes;
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

  private String buildManagedClassesCacheKey(String counselorAccount) {
    return "counselor:classes:" + normalizeKeyPart(counselorAccount);
  }

  private String buildDangerousStudentsCacheKey(String counselorAccount,
      String semester,
      String className) {
    String classPart;

    if (!StringUtils.hasText(className) || "全部".equals(className.trim())) {
      classPart = "ALL";
    } else {
      classPart = className.trim();
    }

    return "warning:dangerous:"
        + normalizeKeyPart(counselorAccount)
        + ":"
        + normalizeKeyPart(semester)
        + ":"
        + normalizeKeyPart(classPart);
  }

  private List<String> readStringListCache(String key) {
    String json = stringRedisTemplate.opsForValue().get(key);
    if (!StringUtils.hasText(json)) {
      return null;
    }

    try {
      return OBJECT_MAPPER.readValue(json, new TypeReference<List<String>>() {
      });
    } catch (Exception e) {
      stringRedisTemplate.delete(key);
      return null;
    }
  }

  private List<CounselorWarningStudentVO> readDangerousStudentsCache(String key) {
    String json = stringRedisTemplate.opsForValue().get(key);
    if (!StringUtils.hasText(json)) {
      return null;
    }

    try {
      List<Map<String, String>> rows = OBJECT_MAPPER.readValue(
          json,
          new TypeReference<List<Map<String, String>>>() {
          }
      );

      return rows.stream()
          .map(row -> CounselorWarningStudentVO.builder()
              .studentId(row.get("studentId"))
              .name(row.get("name"))
              .className(row.get("className"))
              .college(row.get("college"))
              .phone(row.get("phone"))
              .build())
          .collect(Collectors.toList());
    } catch (Exception e) {
      stringRedisTemplate.delete(key);
      return null;
    }
  }

  private void writeDangerousStudentsCache(String key, List<Student> students) {
    List<Map<String, String>> rows = students.stream()
        .map(this::toDangerousStudentCacheRow)
        .collect(Collectors.toList());

    writeJsonCache(key, rows, DANGEROUS_STUDENTS_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
  }

  private Map<String, String> toDangerousStudentCacheRow(Student student) {
    Map<String, String> row = new HashMap<>();
    row.put("studentId", student.getStudentId());
    row.put("name", student.getName());
    row.put("className", student.getClassName());
    row.put("college", student.getCollege());
    row.put("phone", student.getPhone());
    return row;
  }

  private void writeJsonCache(String key, Object value, long timeout, TimeUnit unit) {
    try {
      String json = OBJECT_MAPPER.writeValueAsString(value);
      stringRedisTemplate.opsForValue().set(key, json, timeout, unit);
    } catch (Exception ignored) {
      // Redis 缓存失败不影响主业务查询
    }
  }

  private String normalizeKeyPart(String value) {
    if (!StringUtils.hasText(value)) {
      return "unknown";
    }
    return value.trim().replace(":", "_").replace(" ", "_");
  }

  private void validateCounselorAccount(String counselorAccount) {
    if (!StringUtils.hasText(counselorAccount)) {
      throw new IllegalArgumentException("辅导员账号不能为空");
    }
  }
}