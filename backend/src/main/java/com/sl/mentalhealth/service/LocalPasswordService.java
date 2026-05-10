package com.sl.mentalhealth.service;

import com.sl.mentalhealth.entity.Counselor;
import com.sl.mentalhealth.entity.Student;
import com.sl.mentalhealth.entity.Teacher;
import com.sl.mentalhealth.mapper.CounselorMapper;
import com.sl.mentalhealth.mapper.StudentMapper;
import com.sl.mentalhealth.mapper.TeacherMapper;
import com.sl.mentalhealth.utils.PasswordUtil;
import com.sl.mentalhealth.vo.ResetPasswordResponseVO;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LocalPasswordService {

  /**
   * 每个账号每小时最多允许请求3次密码重置。
   */
  private static final int MAX_RESET_COUNT = 3;
  private static final long RESET_LIMIT_WINDOW_HOURS = 1L;

  private final StudentMapper studentMapper;
  private final TeacherMapper teacherMapper;
  private final CounselorMapper counselorMapper;
  private final StringRedisTemplate stringRedisTemplate;

  public LocalPasswordService(StudentMapper studentMapper,
      TeacherMapper teacherMapper,
      CounselorMapper counselorMapper,
      StringRedisTemplate stringRedisTemplate) {
    this.studentMapper = studentMapper;
    this.teacherMapper = teacherMapper;
    this.counselorMapper = counselorMapper;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  public ResetPasswordResponseVO resetPassword(String role, String username,
      String phone, String newPassword) {

    if (role == null || role.trim().isEmpty()
        || username == null || username.trim().isEmpty()
        || phone == null || phone.trim().isEmpty()
        || newPassword == null || newPassword.trim().isEmpty()) {
      throw new RuntimeException("请填写完整信息");
    }

    role = role.trim().toLowerCase(Locale.ROOT);
    username = username.trim();

    if (!isValidRole(role)) {
      throw new RuntimeException("身份类型错误");
    }

    checkResetPasswordLimit(role, username);

    ResetPasswordResponseVO response = switch (role) {
      case "student" -> resetStudentPassword(username, phone, newPassword);
      case "teacher" -> resetTeacherPassword(username, phone, newPassword);
      case "counselor" -> resetCounselorPassword(username, phone, newPassword);
      default -> throw new RuntimeException("身份类型错误");
    };

    recordResetPasswordRequest(role, username);

    return response;
  }

  private ResetPasswordResponseVO resetStudentPassword(String username, String phone,
      String newPassword) {
    Student student = studentMapper.selectById(username);

    if (student == null) {
      throw new RuntimeException("账号不存在");
    }

    if (!Objects.equals(student.getPhone(), phone)) {
      throw new RuntimeException("手机号验证失败");
    }

    student.setPassword(PasswordUtil.encode(newPassword));
    studentMapper.updateById(student);

    return new ResetPasswordResponseVO(true, "密码重置成功");
  }

  private ResetPasswordResponseVO resetTeacherPassword(String username, String phone,
      String newPassword) {
    Teacher teacher = teacherMapper.selectById(username);

    if (teacher == null) {
      throw new RuntimeException("账号不存在");
    }

    if (!Objects.equals(teacher.getPhone(), phone)) {
      throw new RuntimeException("手机号验证失败");
    }

    teacher.setPassword(PasswordUtil.encode(newPassword));
    teacherMapper.updateById(teacher);

    return new ResetPasswordResponseVO(true, "密码重置成功");
  }

  private ResetPasswordResponseVO resetCounselorPassword(String username, String phone,
      String newPassword) {
    Counselor counselor = counselorMapper.selectById(username);

    if (counselor == null) {
      throw new RuntimeException("账号不存在");
    }

    if (!Objects.equals(counselor.getPhone(), phone)) {
      throw new RuntimeException("手机号验证失败");
    }

    counselor.setPassword(PasswordUtil.encode(newPassword));
    counselorMapper.updateById(counselor);

    return new ResetPasswordResponseVO(true, "密码重置成功");
  }

  private boolean isValidRole(String role) {
    return "student".equals(role)
        || "teacher".equals(role)
        || "counselor".equals(role);
  }

  private void checkResetPasswordLimit(String role, String username) {
    String key = buildResetPasswordLimitKey(role, username);
    long count = getResetPasswordCount(key);

    if (count >= MAX_RESET_COUNT) {
      throw new RuntimeException("密码重置请求过于频繁，请1小时后再试");
    }
  }

  private void recordResetPasswordRequest(String role, String username) {
    String key = buildResetPasswordLimitKey(role, username);

    Long count = stringRedisTemplate.opsForValue().increment(key);

    if (count == null) {
      count = 1L;
    }

    Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);

    if (ttl == null || ttl < 0) {
      stringRedisTemplate.expire(key, RESET_LIMIT_WINDOW_HOURS, TimeUnit.HOURS);
    }

    if (count > MAX_RESET_COUNT) {
      throw new RuntimeException("密码重置请求过于频繁，请1小时后再试");
    }
  }

  private long getResetPasswordCount(String key) {
    String value = stringRedisTemplate.opsForValue().get(key);

    if (value == null || value.trim().isEmpty()) {
      return 0L;
    }

    try {
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      return 0L;
    }
  }

  private String buildResetPasswordLimitKey(String role, String username) {
    return "password:reset:limit:" + normalizeKeyPart(role) + ":" + normalizeKeyPart(username);
  }

  private String normalizeKeyPart(String value) {
    if (value == null || value.trim().isEmpty()) {
      return "unknown";
    }
    return value.trim().replace(":", "_").replace(" ", "_");
  }
}