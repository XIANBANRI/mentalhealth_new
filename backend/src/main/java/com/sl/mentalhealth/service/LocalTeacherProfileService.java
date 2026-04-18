package com.sl.mentalhealth.service;

import com.sl.mentalhealth.entity.Teacher;
import com.sl.mentalhealth.mapper.TeacherMapper;
import com.sl.mentalhealth.vo.TeacherProfileResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class LocalTeacherProfileService {

  private final TeacherMapper teacherMapper;

  public TeacherProfileResponseVO getTeacherProfile(String teacherAccount) {
    if (!StringUtils.hasText(teacherAccount)) {
      throw new RuntimeException("老师账号不能为空");
    }

    Teacher teacher = teacherMapper.selectById(teacherAccount.trim());
    if (teacher == null) {
      throw new RuntimeException("未查询到老师信息");
    }

    return new TeacherProfileResponseVO(
        teacher.getAccount(),
        teacher.getTeacherName(),
        teacher.getPhone(),
        teacher.getOfficeLocation(),
        teacher.getAvatarUrl()
    );
  }

  public TeacherProfileResponseVO updateAvatar(String teacherAccount, String avatarUrl) {
    if (!StringUtils.hasText(teacherAccount)) {
      throw new RuntimeException("老师账号不能为空");
    }
    if (!StringUtils.hasText(avatarUrl)) {
      throw new RuntimeException("头像地址不能为空");
    }

    Teacher teacher = teacherMapper.selectById(teacherAccount.trim());
    if (teacher == null) {
      throw new RuntimeException("未查询到老师信息");
    }

    teacher.setAvatarUrl(avatarUrl.trim());
    teacherMapper.updateById(teacher);

    return new TeacherProfileResponseVO(
        teacher.getAccount(),
        teacher.getTeacherName(),
        teacher.getPhone(),
        teacher.getOfficeLocation(),
        teacher.getAvatarUrl()
    );
  }
}