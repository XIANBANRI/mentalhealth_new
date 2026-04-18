package com.sl.mentalhealth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sl.mentalhealth.dto.TeacherAppointmentQueryRequest;
import com.sl.mentalhealth.dto.TeacherAppointmentUpdateStatusRequest;
import com.sl.mentalhealth.dto.TeacherAssessmentRecordQueryRequest;
import com.sl.mentalhealth.entity.Appointment;
import com.sl.mentalhealth.entity.Student;
import com.sl.mentalhealth.entity.StudentAssessmentRecord;
import com.sl.mentalhealth.entity.Teacher;
import com.sl.mentalhealth.mapper.AppointmentMapper;
import com.sl.mentalhealth.mapper.StudentAssessmentRecordMapper;
import com.sl.mentalhealth.mapper.StudentMapper;
import com.sl.mentalhealth.mapper.TeacherMapper;
import com.sl.mentalhealth.vo.TeacherAppointmentVO;
import com.sl.mentalhealth.vo.TeacherAssessmentRecordVO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class LocalTeacherAppointmentService {

  private final AppointmentMapper appointmentMapper;
  private final StudentAssessmentRecordMapper studentAssessmentRecordMapper;
  private final TeacherMapper teacherMapper;
  private final StudentMapper studentMapper;

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public List<TeacherAppointmentVO> query(TeacherAppointmentQueryRequest request) {
    validateTeacher(request.getTeacherAccount());

    String status = safe(request.getStatus());
    if ("COMPLETED".equals(status) || "CANCELLED".equals(status) || "REJECTED".equals(status)) {
      throw new RuntimeException("预约查询不显示已完成、已取消或已拒绝记录，请到预约记录中查看");
    }

    LocalDate appointmentDate = parseDate(request.getAppointmentDate());

    LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<Appointment>()
        .eq(Appointment::getTeacherAccount, request.getTeacherAccount());

    if (StringUtils.hasText(safe(request.getStudentId()))) {
      wrapper.like(Appointment::getStudentAccount, safe(request.getStudentId()));
    }
    if (appointmentDate != null) {
      wrapper.eq(Appointment::getAppointmentDate, appointmentDate);
    }
    if (StringUtils.hasText(status)) {
      wrapper.eq(Appointment::getStatus, status);
    } else {
      wrapper.notIn(Appointment::getStatus, List.of("COMPLETED", "CANCELLED", "REJECTED"));
    }

    wrapper.orderByDesc(Appointment::getAppointmentDate)
        .orderByDesc(Appointment::getStartTime);

    List<Appointment> list = appointmentMapper.selectList(wrapper);

    return list.stream()
        .filter(item -> {
          String itemStatus = safe(item.getStatus());
          return !"COMPLETED".equals(itemStatus)
                 && !"CANCELLED".equals(itemStatus)
                 && !"REJECTED".equals(itemStatus);
        })
        .map(this::toAppointmentVO)
        .toList();
  }

  public List<TeacherAppointmentVO> record(TeacherAppointmentQueryRequest request) {
    validateTeacher(request.getTeacherAccount());

    LocalDate appointmentDate = parseDate(request.getAppointmentDate());
    String studentId = safe(request.getStudentId());
    String status = safe(request.getStatus());

    LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<Appointment>()
        .eq(Appointment::getTeacherAccount, request.getTeacherAccount());

    if (StringUtils.hasText(studentId)) {
      wrapper.like(Appointment::getStudentAccount, studentId);
    }
    if (appointmentDate != null) {
      wrapper.eq(Appointment::getAppointmentDate, appointmentDate);
    }
    if (StringUtils.hasText(status)) {
      wrapper.eq(Appointment::getStatus, status);
    } else {
      wrapper.ne(Appointment::getStatus, "PENDING");
    }

    wrapper.orderByDesc(Appointment::getAppointmentDate)
        .orderByDesc(Appointment::getStartTime);

    List<Appointment> list = appointmentMapper.selectList(wrapper);
    return list.stream().map(this::toAppointmentVO).toList();
  }

  public TeacherAppointmentVO updateStatus(TeacherAppointmentUpdateStatusRequest request) {
    if (request.getId() == null) {
      throw new RuntimeException("预约ID不能为空");
    }

    validateTeacher(request.getTeacherAccount());

    String targetStatus = safe(request.getStatus());
    if (!StringUtils.hasText(targetStatus)) {
      throw new RuntimeException("状态不能为空");
    }

    Appointment appointment = appointmentMapper.selectOne(new LambdaQueryWrapper<Appointment>()
        .eq(Appointment::getId, request.getId())
        .eq(Appointment::getTeacherAccount, request.getTeacherAccount()));

    if (appointment == null) {
      throw new RuntimeException("未找到对应预约记录");
    }

    String currentStatus = safe(appointment.getStatus());
    String newOfflineRecord = safe(request.getOfflineRecord());
    String newRejectReason = safe(request.getRejectReason());
    LocalDateTime now = LocalDateTime.now();

    switch (targetStatus) {
      case "APPROVED" -> handleApproved(appointment, currentStatus, newOfflineRecord, now);
      case "REJECTED" -> handleRejected(appointment, currentStatus, newRejectReason, now);
      case "COMPLETED" -> handleCompleted(appointment, currentStatus, newOfflineRecord, now);
      default -> throw new RuntimeException("不支持的状态操作");
    }

    appointment.setUpdatedAt(now);
    appointmentMapper.updateById(appointment);
    return toAppointmentVO(appointment);
  }

  private void handleApproved(
      Appointment appointment,
      String currentStatus,
      String newOfflineRecord,
      LocalDateTime now
  ) {
    if ("PENDING".equals(currentStatus)) {
      appointment.setStatus("APPROVED");
      appointment.setApprovedAt(now);
      appointment.setRejectReason(null);
      return;
    }

    if (!"APPROVED".equals(currentStatus)) {
      throw new RuntimeException("只有待处理或已通过预约才能执行该操作");
    }

    if (!StringUtils.hasText(newOfflineRecord)) {
      throw new RuntimeException("线下问诊记录不能为空");
    }

    appointment.setTeacherReply(newOfflineRecord);
  }

  private void handleRejected(
      Appointment appointment,
      String currentStatus,
      String rejectReason,
      LocalDateTime now
  ) {
    if (!"PENDING".equals(currentStatus)) {
      throw new RuntimeException("只有待处理预约才能拒绝");
    }
    if (!StringUtils.hasText(rejectReason)) {
      throw new RuntimeException("拒绝原因不能为空");
    }

    appointment.setStatus("REJECTED");
    appointment.setRejectReason(rejectReason);
    appointment.setTeacherReply(null);
  }

  private void handleCompleted(
      Appointment appointment,
      String currentStatus,
      String newOfflineRecord,
      LocalDateTime now
  ) {
    if (!"APPROVED".equals(currentStatus)) {
      throw new RuntimeException("只有已通过预约才能完成记录");
    }

    String finalRecord = StringUtils.hasText(newOfflineRecord)
        ? newOfflineRecord
        : safe(appointment.getTeacherReply());

    if (!StringUtils.hasText(finalRecord)) {
      throw new RuntimeException("请先填写线下问诊记录，再点击完成");
    }

    appointment.setTeacherReply(finalRecord);
    appointment.setStatus("COMPLETED");
    appointment.setCompletedAt(now);
  }

  public List<TeacherAssessmentRecordVO> assessmentRecord(TeacherAssessmentRecordQueryRequest request) {
    validateTeacher(request.getTeacherAccount());

    String studentId = safe(request.getStudentId());
    if (!StringUtils.hasText(studentId)) {
      throw new RuntimeException("学生学号不能为空");
    }

    Student student = studentMapper.selectById(studentId);
    if (student == null) {
      throw new RuntimeException("未找到对应学生信息");
    }

    List<StudentAssessmentRecord> records =
        studentAssessmentRecordMapper.findByStudentIdOrderBySubmittedAtDescIdDesc(studentId);

    Map<String, List<StudentAssessmentRecord>> semesterGroup = records.stream()
        .collect(Collectors.groupingBy(
            item -> safe(item.getSemester()),
            LinkedHashMap::new,
            Collectors.toList()
        ));

    List<TeacherAssessmentRecordVO> result = new ArrayList<>();

    for (Map.Entry<String, List<StudentAssessmentRecord>> entry : semesterGroup.entrySet()) {
      String semester = entry.getKey();
      List<StudentAssessmentRecord> semesterRecords = entry.getValue();

      semesterRecords.sort((a, b) -> {
        if (a.getSubmittedAt() == null && b.getSubmittedAt() == null) {
          return Long.compare(
              b.getId() == null ? 0L : b.getId(),
              a.getId() == null ? 0L : a.getId()
          );
        }
        if (a.getSubmittedAt() == null) {
          return 1;
        }
        if (b.getSubmittedAt() == null) {
          return -1;
        }
        int compare = b.getSubmittedAt().compareTo(a.getSubmittedAt());
        if (compare != 0) {
          return compare;
        }
        return Long.compare(
            b.getId() == null ? 0L : b.getId(),
            a.getId() == null ? 0L : a.getId()
        );
      });

      List<TeacherAssessmentRecordVO.DetailItem> details = semesterRecords.stream()
          .map(item -> new TeacherAssessmentRecordVO.DetailItem(
              safe(item.getScaleCode()),
              safe(item.getScaleName()),
              item.getRawScore(),
              safe(item.getResultLevel()),
              safe(item.getResultSummary()),
              safe(item.getSuggestion()),
              formatDateTime(item.getSubmittedAt())
          ))
          .toList();

      String scoreSummary = semesterRecords.stream()
          .map(item -> safe(item.getScaleCode()) + ":" + (item.getRawScore() == null ? 0 : item.getRawScore()))
          .collect(Collectors.joining("，"));

      TeacherAssessmentRecordVO vo = new TeacherAssessmentRecordVO();
      vo.setStudentId(student.getStudentId());
      vo.setStudentName(student.getName());
      vo.setCollege(student.getCollege());
      vo.setClassName(student.getClassName());
      vo.setSemester(semester);
      vo.setTestedCount(semesterRecords.size());
      vo.setScoreSummary(scoreSummary);
      vo.setSemesterLevel(calculateSemesterLevel(semesterRecords));
      vo.setDetails(details);

      result.add(vo);
    }

    result.sort(Comparator.comparing(
        TeacherAssessmentRecordVO::getSemester,
        Comparator.nullsLast(String::compareTo)
    ));

    return result;
  }

  private String calculateSemesterLevel(List<StudentAssessmentRecord> records) {
    int maxRisk = records.stream()
        .mapToInt(this::riskWeight)
        .max()
        .orElse(0);

    return switch (maxRisk) {
      case 0 -> "正常";
      case 1 -> "关注";
      case 2 -> "预警";
      default -> "危险";
    };
  }

  private int riskWeight(StudentAssessmentRecord item) {
    String level = safe(item.getResultLevel());
    String scaleCode = safe(item.getScaleCode()).toUpperCase();

    if ("WHO5".equals(scaleCode) || "WHO-5".equals(scaleCode)) {
      if (level.contains("优秀") || level.contains("良好")) {
        return 0;
      }
      if (level.contains("一般")) {
        return 1;
      }
      if (level.contains("较差")) {
        return 3;
      }
      return 1;
    }

    if (level.contains("无") || level.contains("正常") || level.contains("极轻")) {
      return 0;
    }
    if (level.contains("轻")) {
      return 1;
    }
    if (level.contains("中")) {
      return 2;
    }
    if (level.contains("重") || level.contains("严重")) {
      return 3;
    }

    return 1;
  }

  private TeacherAppointmentVO toAppointmentVO(Appointment appointment) {
    String studentId = appointment.getStudentAccount();
    String studentName = "";

    if (StringUtils.hasText(studentId)) {
      Student student = studentMapper.selectById(studentId);
      if (student != null) {
        studentName = student.getName();
      }
    }

    boolean recordCompleted = "COMPLETED".equals(safe(appointment.getStatus()));

    return new TeacherAppointmentVO(
        appointment.getId(),
        appointment.getAppointmentNo(),
        studentId,
        studentName,
        appointment.getTeacherAccount(),
        appointment.getScheduleId(),
        appointment.getAppointmentDate() == null ? "" : appointment.getAppointmentDate().format(DATE_FORMATTER),
        appointment.getStartTime() == null ? "" : appointment.getStartTime().format(TIME_FORMATTER),
        appointment.getEndTime() == null ? "" : appointment.getEndTime().format(TIME_FORMATTER),
        safe(appointment.getPurpose()),
        safe(appointment.getRemark()),
        safe(appointment.getTeacherReply()),
        safe(appointment.getRejectReason()),
        recordCompleted,
        safe(appointment.getStatus()),
        formatDateTime(appointment.getCreatedAt()),
        formatDateTime(appointment.getApprovedAt()),
        formatDateTime(appointment.getCompletedAt())
    );
  }

  private void validateTeacher(String teacherAccount) {
    if (!StringUtils.hasText(teacherAccount)) {
      throw new RuntimeException("老师账号不能为空");
    }

    Teacher teacher = teacherMapper.selectById(teacherAccount);
    if (teacher == null) {
      throw new RuntimeException("老师账号不存在");
    }
  }

  private LocalDate parseDate(String value) {
    String text = safe(value);
    if (!StringUtils.hasText(text)) {
      return null;
    }
    return LocalDate.parse(text);
  }

  private String formatDateTime(LocalDateTime value) {
    return value == null ? "" : value.format(DATE_TIME_FORMATTER);
  }

  private String safe(String value) {
    return value == null ? "" : value.trim();
  }
}