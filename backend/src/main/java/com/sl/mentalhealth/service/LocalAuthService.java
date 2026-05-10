package com.sl.mentalhealth.service;

import com.sl.mentalhealth.common.JwtUtil;
import com.sl.mentalhealth.entity.Admin;
import com.sl.mentalhealth.entity.Counselor;
import com.sl.mentalhealth.entity.Student;
import com.sl.mentalhealth.entity.Teacher;
import com.sl.mentalhealth.mapper.AdminMapper;
import com.sl.mentalhealth.mapper.CounselorMapper;
import com.sl.mentalhealth.mapper.StudentMapper;
import com.sl.mentalhealth.mapper.TeacherMapper;
import com.sl.mentalhealth.utils.PasswordUtil;
import com.sl.mentalhealth.vo.LoginResponseVO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class LocalAuthService {

  /**
   * 5分钟内最多允许失败5次。
   * 第5次失败后，后续请求会被限制，直到Redis key过期。
   */
  private static final int MAX_LOGIN_FAIL_COUNT = 5;
  private static final long LOGIN_FAIL_WINDOW_MINUTES = 5L;

  private final StudentMapper studentMapper;
  private final TeacherMapper teacherMapper;
  private final CounselorMapper counselorMapper;
  private final AdminMapper adminMapper;
  private final StringRedisTemplate stringRedisTemplate;

  public LocalAuthService(StudentMapper studentMapper,
      TeacherMapper teacherMapper,
      CounselorMapper counselorMapper,
      AdminMapper adminMapper,
      StringRedisTemplate stringRedisTemplate) {
    this.studentMapper = studentMapper;
    this.teacherMapper = teacherMapper;
    this.counselorMapper = counselorMapper;
    this.adminMapper = adminMapper;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  public LoginResponseVO login(String role, String username, String password) {
    if (role == null || role.trim().isEmpty()
        || username == null || username.trim().isEmpty()
        || password == null || password.trim().isEmpty()) {
      throw new RuntimeException("请填写完整信息");
    }

    role = role.trim().toLowerCase(Locale.ROOT);
    username = username.trim();

    if (!isValidRole(role)) {
      throw new RuntimeException("身份类型错误");
    }

    String ip = getClientIp();

    // 登录前先检查：账号维度、IP维度是否已经超过限制
    checkLoginFailLimit(role, username, ip);

    try {
      LoginResponseVO response = switch (role) {
        case "student" -> loginStudent(username, password);
        case "teacher" -> loginTeacher(username, password);
        case "counselor" -> loginCounselor(username, password);
        case "admin" -> loginAdmin(username, password);
        default -> throw new RuntimeException("身份类型错误");
      };

      /*
       * 登录成功后清除账号维度的失败次数。
       * IP维度的key会在5分钟后自动过期。
       */
      clearAccountLoginFail(role, username);

      return response;
    } catch (RuntimeException e) {
      if ("账号或密码错误".equals(e.getMessage())) {
        recordLoginFail(role, username, ip);
      }
      throw e;
    }
  }

  private LoginResponseVO loginStudent(String username, String password) {
    Student student = studentMapper.selectById(username);

    if (student != null && PasswordUtil.matches(password, student.getPassword())) {
      // 如果数据库中还是旧的明文密码，登录成功后自动升级为 BCrypt 加密密码
      if (!PasswordUtil.isEncoded(student.getPassword())) {
        student.setPassword(PasswordUtil.encode(password));
        studentMapper.updateById(student);
      }

      String token = JwtUtil.generateToken(username, "student");
      return new LoginResponseVO("student", username, "/student", token);
    }

    throw new RuntimeException("账号或密码错误");
  }

  private LoginResponseVO loginTeacher(String username, String password) {
    Teacher teacher = teacherMapper.selectById(username);

    if (teacher != null && PasswordUtil.matches(password, teacher.getPassword())) {
      // 如果数据库中还是旧的明文密码，登录成功后自动升级为 BCrypt 加密密码
      if (!PasswordUtil.isEncoded(teacher.getPassword())) {
        teacher.setPassword(PasswordUtil.encode(password));
        teacherMapper.updateById(teacher);
      }

      String token = JwtUtil.generateToken(username, "teacher");
      return new LoginResponseVO("teacher", username, "/teacher", token);
    }

    throw new RuntimeException("账号或密码错误");
  }

  private LoginResponseVO loginCounselor(String username, String password) {
    Counselor counselor = counselorMapper.selectById(username);

    if (counselor != null && PasswordUtil.matches(password, counselor.getPassword())) {
      // 如果数据库中还是旧的明文密码，登录成功后自动升级为 BCrypt 加密密码
      if (!PasswordUtil.isEncoded(counselor.getPassword())) {
        counselor.setPassword(PasswordUtil.encode(password));
        counselorMapper.updateById(counselor);
      }

      String token = JwtUtil.generateToken(username, "counselor");
      return new LoginResponseVO("counselor", username, "/counselor", token);
    }

    throw new RuntimeException("账号或密码错误");
  }

  private LoginResponseVO loginAdmin(String username, String password) {
    Admin admin = adminMapper.selectById(username);

    if (admin != null && PasswordUtil.matches(password, admin.getPassword())) {
      // 如果数据库中还是旧的明文密码，登录成功后自动升级为 BCrypt 加密密码
      if (!PasswordUtil.isEncoded(admin.getPassword())) {
        admin.setPassword(PasswordUtil.encode(password));
        adminMapper.updateById(admin);
      }

      String token = JwtUtil.generateToken(username, "admin");
      return new LoginResponseVO("admin", username, "/admin", token);
    }

    throw new RuntimeException("账号或密码错误");
  }

  private boolean isValidRole(String role) {
    return "student".equals(role)
        || "teacher".equals(role)
        || "counselor".equals(role)
        || "admin".equals(role);
  }

  private void checkLoginFailLimit(String role, String username, String ip) {
    String accountKey = buildAccountFailKey(role, username);
    String ipKey = buildIpFailKey(ip);

    long accountFailCount = getFailCount(accountKey);
    long ipFailCount = getFailCount(ipKey);

    if (accountFailCount >= MAX_LOGIN_FAIL_COUNT || ipFailCount >= MAX_LOGIN_FAIL_COUNT) {
      throw new RuntimeException("登录失败次数过多，请5分钟后再试");
    }
  }

  private void recordLoginFail(String role, String username, String ip) {
    String accountKey = buildAccountFailKey(role, username);
    String ipKey = buildIpFailKey(ip);

    long accountFailCount = incrementFailCount(accountKey);
    long ipFailCount = incrementFailCount(ipKey);

    if (accountFailCount >= MAX_LOGIN_FAIL_COUNT || ipFailCount >= MAX_LOGIN_FAIL_COUNT) {
      throw new RuntimeException("登录失败次数过多，请5分钟后再试");
    }
  }

  private long incrementFailCount(String key) {
    Long count = stringRedisTemplate.opsForValue().increment(key);

    if (count == null) {
      count = 1L;
    }

    Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);

    // 第一次创建key，或者异常情况下key没有过期时间时，补上5分钟过期时间
    if (ttl == null || ttl < 0) {
      stringRedisTemplate.expire(key, LOGIN_FAIL_WINDOW_MINUTES, TimeUnit.MINUTES);
    }

    return count;
  }

  private long getFailCount(String key) {
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

  private void clearAccountLoginFail(String role, String username) {
    stringRedisTemplate.delete(buildAccountFailKey(role, username));
  }

  private String buildAccountFailKey(String role, String username) {
    return "login:fail:account:" + normalizeKeyPart(role) + ":" + normalizeKeyPart(username);
  }

  private String buildIpFailKey(String ip) {
    return "login:fail:ip:" + normalizeKeyPart(ip);
  }

  private String normalizeKeyPart(String value) {
    if (value == null || value.trim().isEmpty()) {
      return "unknown";
    }
    return value.trim().replace(":", "_").replace(" ", "_");
  }

  private String getClientIp() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    if (attributes == null) {
      return "unknown";
    }

    HttpServletRequest request = attributes.getRequest();

    String ip = request.getHeader("X-Forwarded-For");
    if (ip != null && !ip.trim().isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
      return ip.split(",")[0].trim();
    }

    ip = request.getHeader("X-Real-IP");
    if (ip != null && !ip.trim().isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
      return ip.trim();
    }

    ip = request.getHeader("Proxy-Client-IP");
    if (ip != null && !ip.trim().isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
      return ip.trim();
    }

    ip = request.getHeader("WL-Proxy-Client-IP");
    if (ip != null && !ip.trim().isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
      return ip.trim();
    }

    return request.getRemoteAddr();
  }
}