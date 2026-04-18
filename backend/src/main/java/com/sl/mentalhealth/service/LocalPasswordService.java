package com.sl.mentalhealth.service;

import com.sl.mentalhealth.entity.Counselor;
import com.sl.mentalhealth.entity.Student;
import com.sl.mentalhealth.entity.Teacher;
import com.sl.mentalhealth.mapper.CounselorMapper;
import com.sl.mentalhealth.mapper.StudentMapper;
import com.sl.mentalhealth.mapper.TeacherMapper;
import com.sl.mentalhealth.vo.ResetPasswordResponseVO;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class LocalPasswordService {

  private final StudentMapper studentMapper;
  private final TeacherMapper teacherMapper;
  private final CounselorMapper counselorMapper;

  public LocalPasswordService(StudentMapper studentMapper,
      TeacherMapper teacherMapper,
      CounselorMapper counselorMapper) {
    this.studentMapper = studentMapper;
    this.teacherMapper = teacherMapper;
    this.counselorMapper = counselorMapper;
  }

  public ResetPasswordResponseVO resetPassword(String role, String username,
      String phone, String newPassword) {

    if (role == null || role.trim().isEmpty()
        || username == null || username.trim().isEmpty()
        || phone == null || phone.trim().isEmpty()
        || newPassword == null || newPassword.trim().isEmpty()) {
      throw new RuntimeException("请填写完整信息");
    }

    switch (role) {
      case "student":
        return resetStudentPassword(username, phone, newPassword);

      case "teacher":
        return resetTeacherPassword(username, phone, newPassword);

      case "counselor":
        return resetCounselorPassword(username, phone, newPassword);

      default:
        throw new RuntimeException("身份类型错误");
    }
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

    student.setPassword(newPassword);
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

    teacher.setPassword(newPassword);
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

    counselor.setPassword(newPassword);
    counselorMapper.updateById(counselor);

    return new ResetPasswordResponseVO(true, "密码重置成功");
  }
}