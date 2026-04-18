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
import com.sl.mentalhealth.vo.LoginResponseVO;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class LocalAuthService {

  private final StudentMapper studentMapper;
  private final TeacherMapper teacherMapper;
  private final CounselorMapper counselorMapper;
  private final AdminMapper adminMapper;

  public LocalAuthService(StudentMapper studentMapper,
      TeacherMapper teacherMapper,
      CounselorMapper counselorMapper,
      AdminMapper adminMapper) {
    this.studentMapper = studentMapper;
    this.teacherMapper = teacherMapper;
    this.counselorMapper = counselorMapper;
    this.adminMapper = adminMapper;
  }

  public LoginResponseVO login(String role, String username, String password) {
    if (role == null || role.trim().isEmpty()
        || username == null || username.trim().isEmpty()
        || password == null || password.trim().isEmpty()) {
      throw new RuntimeException("请填写完整信息");
    }

    return switch (role) {
      case "student" -> loginStudent(username, password);
      case "teacher" -> loginTeacher(username, password);
      case "counselor" -> loginCounselor(username, password);
      case "admin" -> loginAdmin(username, password);
      default -> throw new RuntimeException("身份类型错误");
    };
  }

  private LoginResponseVO loginStudent(String username, String password) {
    Student student = studentMapper.selectById(username);

    if (student != null && Objects.equals(student.getPassword(), password)) {
      String token = JwtUtil.generateToken(username, "student");
      return new LoginResponseVO("student", username, "/student", token);
    }

    throw new RuntimeException("账号或密码错误");
  }

  private LoginResponseVO loginTeacher(String username, String password) {
    Teacher teacher = teacherMapper.selectById(username);

    if (teacher != null && Objects.equals(teacher.getPassword(), password)) {
      String token = JwtUtil.generateToken(username, "teacher");
      return new LoginResponseVO("teacher", username, "/teacher", token);
    }

    throw new RuntimeException("账号或密码错误");
  }

  private LoginResponseVO loginCounselor(String username, String password) {
    Counselor counselor = counselorMapper.selectById(username);

    if (counselor != null && Objects.equals(counselor.getPassword(), password)) {
      String token = JwtUtil.generateToken(username, "counselor");
      return new LoginResponseVO("counselor", username, "/counselor", token);
    }

    throw new RuntimeException("账号或密码错误");
  }

  private LoginResponseVO loginAdmin(String username, String password) {
    Admin admin = adminMapper.selectById(username);

    if (admin != null && Objects.equals(admin.getPassword(), password)) {
      String token = JwtUtil.generateToken(username, "admin");
      return new LoginResponseVO("admin", username, "/admin", token);
    }

    throw new RuntimeException("账号或密码错误");
  }
}