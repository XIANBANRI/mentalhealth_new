package com.sl.mentalhealth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sl.mentalhealth.entity.Appointment;
import com.sl.mentalhealth.entity.Student;
import com.sl.mentalhealth.entity.Teacher;
import com.sl.mentalhealth.entity.TeacherSchedule;
import com.sl.mentalhealth.mapper.AppointmentMapper;
import com.sl.mentalhealth.mapper.StudentMapper;
import com.sl.mentalhealth.mapper.TeacherMapper;
import com.sl.mentalhealth.mapper.TeacherScheduleMapper;
import com.sl.mentalhealth.vo.AppointmentVO;
import com.sl.mentalhealth.vo.AvailableAppointmentVO;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class LocalAppointmentService {

  private static final Integer STATUS_ENABLED = 1;

  private static final List<String> OCCUPIED_STATUS = List.of("PENDING", "APPROVED", "COMPLETED");
  private static final List<String> DUPLICATE_STATUS = List.of("PENDING", "APPROVED");

  private static final String AVAILABLE_APPOINTMENT_CACHE_NAME = "availableAppointment";

  /**
   * 预约创建分布式锁配置。
   * 最多等待3秒获取锁。
   * 锁自动过期10秒，防止服务异常导致死锁。
   * 每100毫秒重试一次。
   */
  private static final long APPOINTMENT_LOCK_WAIT_MILLIS = 3000L;
  private static final long APPOINTMENT_LOCK_RETRY_INTERVAL_MILLIS = 100L;
  private static final long APPOINTMENT_LOCK_EXPIRE_SECONDS = 10L;

  private final AppointmentMapper appointmentMapper;
  private final TeacherScheduleMapper teacherScheduleMapper;
  private final TeacherMapper teacherMapper;
  private final StudentMapper studentMapper;
  private final StringRedisTemplate stringRedisTemplate;
  private final TransactionTemplate transactionTemplate;

  @Cacheable(
      cacheNames = AVAILABLE_APPOINTMENT_CACHE_NAME,
      key = "#dateStr == null || #dateStr.isBlank() ? T(java.time.LocalDate).now().toString() : #dateStr.trim()"
  )
  public List<AvailableAppointmentVO> studentAvailable(String dateStr) {
    LocalDate date = parseAppointmentDate(dateStr);
    int weekDay = convertWeekDay(date.getDayOfWeek());

    List<TeacherSchedule> schedules =
        teacherScheduleMapper.selectByWeekDayAndStatusOrderByStartTimeAsc(weekDay, STATUS_ENABLED);

    if (schedules == null || schedules.isEmpty()) {
      return Collections.emptyList();
    }

    Set<String> teacherAccounts = new HashSet<>();
    for (TeacherSchedule schedule : schedules) {
      if (schedule.getTeacherAccount() != null && !schedule.getTeacherAccount().isBlank()) {
        teacherAccounts.add(schedule.getTeacherAccount());
      }
    }

    Map<String, Teacher> teacherMap = buildTeacherMap(teacherAccounts);

    List<AvailableAppointmentVO> result = new ArrayList<>();
    for (TeacherSchedule schedule : schedules) {
      Teacher teacher = teacherMap.get(schedule.getTeacherAccount());

      long used = countAppointments(new LambdaQueryWrapper<Appointment>()
          .eq(Appointment::getScheduleId, schedule.getId())
          .eq(Appointment::getAppointmentDate, date)
          .in(Appointment::getStatus, OCCUPIED_STATUS));

      result.add(buildAvailableAppointmentVO(schedule, teacher, used));
    }

    return result;
  }

  @CacheEvict(
      cacheNames = AVAILABLE_APPOINTMENT_CACHE_NAME,
      key = "#appointmentDateStr.trim()"
  )
  public Long studentCreate(
      String studentId,
      Long scheduleId,
      String appointmentDateStr,
      String purpose,
      String remark
  ) {
    if (studentId == null || studentId.isBlank()) {
      throw new RuntimeException("学生学号不能为空");
    }
    if (scheduleId == null) {
      throw new RuntimeException("排班ID不能为空");
    }
    if (appointmentDateStr == null || appointmentDateStr.isBlank()) {
      throw new RuntimeException("预约日期不能为空");
    }

    LocalDate appointmentDate = LocalDate.parse(appointmentDateStr.trim());
    if (appointmentDate.isBefore(LocalDate.now())) {
      throw new RuntimeException("不能预约过去日期");
    }

    String lockKey = buildAppointmentCreateLockKey(scheduleId);
    String lockValue = UUID.randomUUID().toString();

    boolean locked = tryAcquireLock(lockKey, lockValue);
    if (!locked) {
      throw new RuntimeException("当前预约人数较多，请稍后重试");
    }

    try {
      Long appointmentId = transactionTemplate.execute(status ->
          doStudentCreate(studentId, scheduleId, appointmentDate, purpose, remark)
      );

      if (appointmentId == null) {
        throw new RuntimeException("预约失败");
      }

      return appointmentId;
    } finally {
      releaseLock(lockKey, lockValue);
    }
  }

  private Long doStudentCreate(
      String studentId,
      Long scheduleId,
      LocalDate appointmentDate,
      String purpose,
      String remark
  ) {
    Student student = studentMapper.selectById(studentId);
    if (student == null) {
      throw new RuntimeException("学生不存在");
    }

    TeacherSchedule schedule = teacherScheduleMapper.selectById(scheduleId);
    if (schedule == null) {
      throw new RuntimeException("排班不存在");
    }

    if (!Objects.equals(schedule.getStatus(), STATUS_ENABLED)) {
      throw new RuntimeException("该工作时间已停用，无法预约");
    }

    int weekDay = convertWeekDay(appointmentDate.getDayOfWeek());
    if (!Objects.equals(schedule.getWeekDay(), weekDay)) {
      throw new RuntimeException("预约日期与老师排班星期不匹配");
    }

    boolean duplicated = countAppointments(new LambdaQueryWrapper<Appointment>()
        .eq(Appointment::getStudentAccount, studentId)
        .eq(Appointment::getScheduleId, scheduleId)
        .eq(Appointment::getAppointmentDate, appointmentDate)
        .in(Appointment::getStatus, DUPLICATE_STATUS)) > 0;
    if (duplicated) {
      throw new RuntimeException("你已预约过该时段");
    }

    boolean sameTimeDuplicated = countAppointments(new LambdaQueryWrapper<Appointment>()
        .eq(Appointment::getStudentAccount, studentId)
        .eq(Appointment::getAppointmentDate, appointmentDate)
        .eq(Appointment::getStartTime, schedule.getStartTime())
        .eq(Appointment::getEndTime, schedule.getEndTime())
        .in(Appointment::getStatus, DUPLICATE_STATUS)) > 0;
    if (sameTimeDuplicated) {
      throw new RuntimeException("同一天同一时间段只能预约一个老师");
    }

    long used = countAppointments(new LambdaQueryWrapper<Appointment>()
        .eq(Appointment::getScheduleId, scheduleId)
        .eq(Appointment::getAppointmentDate, appointmentDate)
        .in(Appointment::getStatus, OCCUPIED_STATUS));
    if (used >= schedule.getMaxAppointments()) {
      throw new RuntimeException("该时段预约人数已满");
    }

    Appointment appointment = new Appointment();
    appointment.setAppointmentNo(generateAppointmentNo());
    appointment.setStudentAccount(studentId);
    appointment.setTeacherAccount(schedule.getTeacherAccount());
    appointment.setScheduleId(scheduleId);
    appointment.setAppointmentDate(appointmentDate);
    appointment.setStartTime(schedule.getStartTime());
    appointment.setEndTime(schedule.getEndTime());
    appointment.setPurpose(purpose);
    appointment.setRemark(remark);
    appointment.setStatus("PENDING");
    appointment.setCreatedAt(LocalDateTime.now());
    appointment.setUpdatedAt(LocalDateTime.now());

    appointmentMapper.insert(appointment);
    return appointment.getId();
  }

  public List<AppointmentVO> studentMy(String studentId) {
    List<Appointment> list = appointmentMapper.selectList(new LambdaQueryWrapper<Appointment>()
        .eq(Appointment::getStudentAccount, studentId)
        .orderByDesc(Appointment::getAppointmentDate)
        .orderByDesc(Appointment::getStartTime));
    return buildAppointmentVOList(list);
  }

  @CacheEvict(cacheNames = AVAILABLE_APPOINTMENT_CACHE_NAME, allEntries = true)
  @Transactional(rollbackFor = Exception.class)
  public void studentCancel(Long appointmentId, String studentId) {
    Appointment appointment = appointmentMapper.selectOne(new LambdaQueryWrapper<Appointment>()
        .eq(Appointment::getId, appointmentId)
        .eq(Appointment::getStudentAccount, studentId));

    if (appointment == null) {
      throw new RuntimeException("预约记录不存在");
    }

    if ("CANCELLED".equals(appointment.getStatus())) {
      throw new RuntimeException("该预约已取消");
    }
    if ("REJECTED".equals(appointment.getStatus())) {
      throw new RuntimeException("该预约已被拒绝，不能取消");
    }
    if ("COMPLETED".equals(appointment.getStatus())) {
      throw new RuntimeException("该预约已完成，不能取消");
    }

    appointment.setStatus("CANCELLED");
    appointment.setCancelledAt(LocalDateTime.now());
    appointment.setUpdatedAt(LocalDateTime.now());
    appointmentMapper.updateById(appointment);
  }

  public List<AppointmentVO> teacherList(String teacherAccount, String status, String dateStr) {
    if (teacherAccount == null || teacherAccount.isBlank()) {
      throw new RuntimeException("老师账号不能为空");
    }

    LocalDate date = null;
    if (dateStr != null && !dateStr.isBlank()) {
      date = LocalDate.parse(dateStr.trim());
    }

    LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<Appointment>()
        .eq(Appointment::getTeacherAccount, teacherAccount)
        .notIn(Appointment::getStatus, List.of("COMPLETED", "CANCELLED"));

    if (status != null && !status.isBlank()) {
      wrapper.eq(Appointment::getStatus, status);
    }
    if (date != null) {
      wrapper.eq(Appointment::getAppointmentDate, date);
    }

    wrapper.orderByDesc(Appointment::getAppointmentDate)
        .orderByDesc(Appointment::getStartTime);

    List<Appointment> list = appointmentMapper.selectList(wrapper);
    return buildAppointmentVOList(list);
  }

  @Transactional(rollbackFor = Exception.class)
  public void teacherApprove(Long appointmentId, String teacherAccount, String teacherReply) {
    Appointment appointment = appointmentMapper.selectOne(new LambdaQueryWrapper<Appointment>()
        .eq(Appointment::getId, appointmentId)
        .eq(Appointment::getTeacherAccount, teacherAccount));

    if (appointment == null) {
      throw new RuntimeException("预约记录不存在");
    }

    if (!"PENDING".equals(appointment.getStatus())) {
      throw new RuntimeException("只有待审核预约才能通过");
    }

    appointment.setStatus("APPROVED");
    appointment.setTeacherReply(teacherReply);
    appointment.setRejectReason(null);
    appointment.setApprovedAt(LocalDateTime.now());
    appointment.setUpdatedAt(LocalDateTime.now());

    appointmentMapper.updateById(appointment);
  }

  @CacheEvict(cacheNames = AVAILABLE_APPOINTMENT_CACHE_NAME, allEntries = true)
  @Transactional(rollbackFor = Exception.class)
  public void teacherReject(Long appointmentId, String teacherAccount, String rejectReason) {
    Appointment appointment = appointmentMapper.selectOne(new LambdaQueryWrapper<Appointment>()
        .eq(Appointment::getId, appointmentId)
        .eq(Appointment::getTeacherAccount, teacherAccount));

    if (appointment == null) {
      throw new RuntimeException("预约记录不存在");
    }

    if (!"PENDING".equals(appointment.getStatus())) {
      throw new RuntimeException("只有待审核预约才能拒绝");
    }
    if (rejectReason == null || rejectReason.isBlank()) {
      throw new RuntimeException("拒绝原因不能为空");
    }

    appointment.setStatus("REJECTED");
    appointment.setRejectReason(rejectReason);
    appointment.setTeacherReply(null);
    appointment.setUpdatedAt(LocalDateTime.now());

    appointmentMapper.updateById(appointment);
  }

  @Transactional(rollbackFor = Exception.class)
  public void teacherComplete(Long appointmentId, String teacherAccount, String teacherReply) {
    Appointment appointment = appointmentMapper.selectOne(new LambdaQueryWrapper<Appointment>()
        .eq(Appointment::getId, appointmentId)
        .eq(Appointment::getTeacherAccount, teacherAccount));

    if (appointment == null) {
      throw new RuntimeException("预约记录不存在");
    }

    if (!"APPROVED".equals(appointment.getStatus())) {
      throw new RuntimeException("只有已通过预约才能完成");
    }

    appointment.setStatus("COMPLETED");
    appointment.setTeacherReply(teacherReply);
    appointment.setCompletedAt(LocalDateTime.now());
    appointment.setUpdatedAt(LocalDateTime.now());

    appointmentMapper.updateById(appointment);
  }

  private AvailableAppointmentVO buildAvailableAppointmentVO(
      TeacherSchedule schedule,
      Teacher teacher,
      long used
  ) {
    AvailableAppointmentVO vo = new AvailableAppointmentVO();
    vo.setScheduleId(schedule.getId());
    vo.setTeacherAccount(schedule.getTeacherAccount());
    vo.setTeacherName(teacher == null ? null : teacher.getTeacherName());
    vo.setOfficeLocation(teacher == null ? null : teacher.getOfficeLocation());
    vo.setPhone(teacher == null ? null : teacher.getPhone());
    vo.setWeekDay(schedule.getWeekDay());
    vo.setStartTime(schedule.getStartTime() == null ? null : schedule.getStartTime().toString());
    vo.setEndTime(schedule.getEndTime() == null ? null : schedule.getEndTime().toString());
    vo.setMaxAppointments(schedule.getMaxAppointments());
    vo.setUsedAppointments((int) used);
    vo.setRemainingAppointments(Math.max(schedule.getMaxAppointments() - (int) used, 0));
    vo.setRemark(schedule.getRemark());
    return vo;
  }

  private List<AppointmentVO> buildAppointmentVOList(List<Appointment> appointments) {
    if (appointments == null || appointments.isEmpty()) {
      return Collections.emptyList();
    }

    Set<String> teacherAccounts = new HashSet<>();
    Set<String> studentAccounts = new HashSet<>();

    for (Appointment item : appointments) {
      if (item.getTeacherAccount() != null && !item.getTeacherAccount().isBlank()) {
        teacherAccounts.add(item.getTeacherAccount());
      }
      if (item.getStudentAccount() != null && !item.getStudentAccount().isBlank()) {
        studentAccounts.add(item.getStudentAccount());
      }
    }

    Map<String, Teacher> teacherMap = buildTeacherMap(teacherAccounts);
    Map<String, Student> studentMap = buildStudentMap(studentAccounts);

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    List<AppointmentVO> result = new ArrayList<>();
    for (Appointment item : appointments) {
      Teacher teacher = teacherMap.get(item.getTeacherAccount());
      Student student = studentMap.get(item.getStudentAccount());

      AppointmentVO vo = new AppointmentVO();
      vo.setId(item.getId());
      vo.setAppointmentNo(item.getAppointmentNo());

      vo.setStudentAccount(item.getStudentAccount());
      vo.setStudentName(student == null ? null : student.getName());
      vo.setCollege(student == null ? null : student.getCollege());
      vo.setClassName(student == null ? null : student.getClassName());

      vo.setTeacherAccount(item.getTeacherAccount());
      vo.setTeacherName(teacher == null ? null : teacher.getTeacherName());
      vo.setOfficeLocation(teacher == null ? null : teacher.getOfficeLocation());
      vo.setTeacherPhone(teacher == null ? null : teacher.getPhone());

      vo.setScheduleId(item.getScheduleId());
      vo.setAppointmentDate(item.getAppointmentDate() == null ? null : item.getAppointmentDate().toString());
      vo.setStartTime(item.getStartTime() == null ? null : item.getStartTime().toString());
      vo.setEndTime(item.getEndTime() == null ? null : item.getEndTime().toString());

      vo.setPurpose(item.getPurpose());
      vo.setRemark(item.getRemark());
      vo.setTeacherReply(item.getTeacherReply());
      vo.setRejectReason(item.getRejectReason());
      vo.setStatus(item.getStatus());

      vo.setCreatedAt(item.getCreatedAt() == null ? null : item.getCreatedAt().format(dateTimeFormatter));
      vo.setApprovedAt(item.getApprovedAt() == null ? null : item.getApprovedAt().format(dateTimeFormatter));
      vo.setCancelledAt(item.getCancelledAt() == null ? null : item.getCancelledAt().format(dateTimeFormatter));
      vo.setCompletedAt(item.getCompletedAt() == null ? null : item.getCompletedAt().format(dateTimeFormatter));

      result.add(vo);
    }

    return result;
  }

  private Map<String, Teacher> buildTeacherMap(Set<String> teacherAccounts) {
    Map<String, Teacher> teacherMap = new HashMap<>();
    if (teacherAccounts == null || teacherAccounts.isEmpty()) {
      return teacherMap;
    }

    for (Teacher teacher : teacherMapper.selectByIds(teacherAccounts)) {
      teacherMap.put(teacher.getAccount(), teacher);
    }
    return teacherMap;
  }

  private Map<String, Student> buildStudentMap(Set<String> studentAccounts) {
    Map<String, Student> studentMap = new HashMap<>();
    if (studentAccounts == null || studentAccounts.isEmpty()) {
      return studentMap;
    }

    for (Student student : studentMapper.selectByIds(studentAccounts)) {
      studentMap.put(student.getStudentId(), student);
    }
    return studentMap;
  }

  private LocalDate parseAppointmentDate(String dateStr) {
    if (dateStr == null || dateStr.isBlank()) {
      return LocalDate.now();
    }
    return LocalDate.parse(dateStr.trim());
  }

  private int convertWeekDay(DayOfWeek dayOfWeek) {
    return switch (dayOfWeek) {
      case MONDAY -> 1;
      case TUESDAY -> 2;
      case WEDNESDAY -> 3;
      case THURSDAY -> 4;
      case FRIDAY -> 5;
      case SATURDAY -> 6;
      case SUNDAY -> 7;
    };
  }

  private String buildAppointmentCreateLockKey(Long scheduleId) {
    return "schedule:" + scheduleId + ":create";
  }

  private boolean tryAcquireLock(String lockKey, String lockValue) {
    long deadline = System.currentTimeMillis() + APPOINTMENT_LOCK_WAIT_MILLIS;

    while (System.currentTimeMillis() < deadline) {
      Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(
          lockKey,
          lockValue,
          APPOINTMENT_LOCK_EXPIRE_SECONDS,
          TimeUnit.SECONDS
      );

      if (Boolean.TRUE.equals(success)) {
        return true;
      }

      try {
        Thread.sleep(APPOINTMENT_LOCK_RETRY_INTERVAL_MILLIS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("预约请求被中断，请稍后重试");
      }
    }

    return false;
  }

  private void releaseLock(String lockKey, String lockValue) {
    DefaultRedisScript<Long> script = new DefaultRedisScript<>();
    script.setScriptText(
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) " +
            "else return 0 end"
    );
    script.setResultType(Long.class);

    stringRedisTemplate.execute(script, Collections.singletonList(lockKey), lockValue);
  }

  private String generateAppointmentNo() {
    return "APT" + System.currentTimeMillis();
  }

  private long countAppointments(LambdaQueryWrapper<Appointment> wrapper) {
    Long count = appointmentMapper.selectCount(wrapper);
    return count == null ? 0L : count;
  }
}