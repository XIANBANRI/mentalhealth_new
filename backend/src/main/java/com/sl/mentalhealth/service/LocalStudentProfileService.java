package com.sl.mentalhealth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sl.mentalhealth.entity.Counselor;
import com.sl.mentalhealth.entity.CounselorClassMapping;
import com.sl.mentalhealth.entity.Student;
import com.sl.mentalhealth.kafka.message.StudentProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.StudentProfileResponseMessage;
import com.sl.mentalhealth.mapper.CounselorClassMappingMapper;
import com.sl.mentalhealth.mapper.CounselorMapper;
import com.sl.mentalhealth.mapper.StudentMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LocalStudentProfileService {

  private final StudentMapper studentMapper;
  private final CounselorMapper counselorMapper;
  private final CounselorClassMappingMapper counselorClassMappingMapper;

  public LocalStudentProfileService(StudentMapper studentMapper,
      CounselorMapper counselorMapper,
      CounselorClassMappingMapper counselorClassMappingMapper) {
    this.studentMapper = studentMapper;
    this.counselorMapper = counselorMapper;
    this.counselorClassMappingMapper = counselorClassMappingMapper;
  }

  public StudentProfileResponseMessage handle(StudentProfileRequestMessage request) {
    if (StudentProfileRequestMessage.ACTION_UPDATE_AVATAR.equals(request.getAction())) {
      return updateAvatar(request);
    }
    return queryProfile(request);
  }

  public StudentProfileResponseMessage queryProfile(StudentProfileRequestMessage request) {
    String requestId = request.getRequestId();
    String studentId = request.getStudentId();

    if (studentId == null || studentId.trim().isEmpty()) {
      return new StudentProfileResponseMessage(
          requestId, false, "学号不能为空",
          null, null, null, null, null, null, null, null, null
      );
    }

    Student student = studentMapper.selectById(studentId.trim());

    if (student == null) {
      return new StudentProfileResponseMessage(
          requestId, false, "学生不存在",
          studentId, null, null, null, null, null, null, null, null
      );
    }

    CounselorContact counselorContact = resolveCounselorContact(student);

    return buildSuccess(requestId, "查询成功", student, counselorContact);
  }

  public StudentProfileResponseMessage updateAvatar(StudentProfileRequestMessage request) {
    String requestId = request.getRequestId();
    String studentId = request.getStudentId();
    String avatarUrl = request.getAvatarUrl();

    if (studentId == null || studentId.trim().isEmpty()) {
      return new StudentProfileResponseMessage(
          requestId, false, "学号不能为空",
          null, null, null, null, null, null, null, null, null
      );
    }

    if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
      return new StudentProfileResponseMessage(
          requestId, false, "头像地址不能为空",
          studentId, null, null, null, null, null, null, null, null
      );
    }

    Student student = studentMapper.selectById(studentId.trim());

    if (student == null) {
      return new StudentProfileResponseMessage(
          requestId, false, "学生不存在",
          studentId, null, null, null, null, null, null, null, null
      );
    }

    student.setAvatarUrl(avatarUrl.trim());
    studentMapper.updateById(student);

    CounselorContact counselorContact = resolveCounselorContact(student);
    return buildSuccess(requestId, "头像上传成功", student, counselorContact);
  }

  private StudentProfileResponseMessage buildSuccess(String requestId, String message,
      Student student, CounselorContact counselorContact) {
    return new StudentProfileResponseMessage(
        requestId,
        true,
        message,
        student.getStudentId(),
        student.getName(),
        student.getClassName(),
        student.getCollege(),
        student.getGrade(),
        student.getPhone(),
        student.getAvatarUrl(),
        counselorContact.name(),
        counselorContact.phone()
    );
  }

  private CounselorContact resolveCounselorContact(Student student) {
    String counselorName = null;
    String counselorPhone = null;

    if (student.getClassName() != null && !student.getClassName().trim().isEmpty()) {
      List<CounselorClassMapping> mappings = counselorClassMappingMapper.selectList(
          Wrappers.<CounselorClassMapping>lambdaQuery()
              .eq(CounselorClassMapping::getClassName, student.getClassName().trim())
              .last("LIMIT 1")
      );

      if (!mappings.isEmpty()) {
        CounselorClassMapping mapping = mappings.get(0);

        Counselor counselor = counselorMapper.selectById(mapping.getCounselorAccount());

        if (counselor != null) {
          if (student.getGrade() != null && student.getGrade().equals(counselor.getGrade())) {
            counselorName = counselor.getName();
            counselorPhone = counselor.getPhone();
          }
        }
      }
    }

    return new CounselorContact(counselorName, counselorPhone);
  }

  private record CounselorContact(String name, String phone) {
  }
}