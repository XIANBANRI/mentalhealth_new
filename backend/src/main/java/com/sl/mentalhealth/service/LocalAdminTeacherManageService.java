package com.sl.mentalhealth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sl.mentalhealth.dto.AdminTeacherCreateRequest;
import com.sl.mentalhealth.dto.AdminTeacherQueryRequest;
import com.sl.mentalhealth.dto.AdminTeacherUpdateRequest;
import com.sl.mentalhealth.entity.Teacher;
import com.sl.mentalhealth.kafka.message.AdminTeacherManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminTeacherManageResponseMessage;
import com.sl.mentalhealth.mapper.TeacherMapper;
import com.sl.mentalhealth.vo.AdminTeacherPageVO;
import com.sl.mentalhealth.vo.AdminTeacherVO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class LocalAdminTeacherManageService {

  private final TeacherMapper teacherMapper;

  public LocalAdminTeacherManageService(TeacherMapper teacherMapper) {
    this.teacherMapper = teacherMapper;
  }

  public AdminTeacherManageResponseMessage handle(AdminTeacherManageRequestMessage requestMessage) {
    try {
      if (requestMessage == null || !StringUtils.hasText(requestMessage.getAction())) {
        return fail(null, "请求参数不正确");
      }

      String action = requestMessage.getAction();

      if (AdminTeacherManageRequestMessage.ACTION_QUERY_PAGE.equals(action)) {
        AdminTeacherPageVO pageVO = queryPage(requestMessage.getQueryRequest());
        return successPage(requestMessage.getRequestId(), "查询成功", pageVO);
      }

      if (AdminTeacherManageRequestMessage.ACTION_DETAIL.equals(action)) {
        AdminTeacherVO teacherVO = detail(requestMessage.getAccount());
        return successTeacher(requestMessage.getRequestId(), "查询成功", teacherVO);
      }

      if (AdminTeacherManageRequestMessage.ACTION_CREATE.equals(action)) {
        AdminTeacherVO teacherVO = create(requestMessage.getCreateRequest());
        return successTeacher(requestMessage.getRequestId(), "新增成功", teacherVO);
      }

      if (AdminTeacherManageRequestMessage.ACTION_UPDATE.equals(action)) {
        AdminTeacherVO teacherVO = update(requestMessage.getUpdateRequest());
        return successTeacher(requestMessage.getRequestId(), "修改成功", teacherVO);
      }

      return fail(requestMessage.getRequestId(), "不支持的操作类型");
    } catch (Exception e) {
      return fail(
          requestMessage == null ? null : requestMessage.getRequestId(),
          e.getMessage() == null ? "操作失败" : e.getMessage()
      );
    }
  }

  private AdminTeacherPageVO queryPage(AdminTeacherQueryRequest request) {
    AdminTeacherQueryRequest queryRequest = request == null ? new AdminTeacherQueryRequest() : request;

    int pageNum = queryRequest.getPageNum() == null || queryRequest.getPageNum() < 1
        ? 1 : queryRequest.getPageNum();
    int pageSize = queryRequest.getPageSize() == null || queryRequest.getPageSize() < 1
        ? 10 : queryRequest.getPageSize();

    Page<Teacher> page = new Page<>(pageNum, pageSize);

    Page<Teacher> teacherPage = teacherMapper.selectPageByCondition(
        page,
        normalize(queryRequest.getAccount()),
        normalize(queryRequest.getTeacherName()),
        normalize(queryRequest.getOfficeLocation()),
        normalize(queryRequest.getPhone())
    );

    List<AdminTeacherVO> list = new ArrayList<>();
    for (Teacher teacher : teacherPage.getRecords()) {
      list.add(toVO(teacher));
    }

    AdminTeacherPageVO pageVO = new AdminTeacherPageVO();
    pageVO.setTotal(teacherPage.getTotal());
    pageVO.setPageNum(pageNum);
    pageVO.setPageSize(pageSize);
    pageVO.setList(list);
    return pageVO;
  }

  private AdminTeacherVO detail(String account) {
    String teacherAccount = required(account, "老师账号不能为空");
    Teacher teacher = teacherMapper.selectById(teacherAccount);
    if (teacher == null) {
      throw new RuntimeException("心理老师不存在");
    }
    return toVO(teacher);
  }

  private AdminTeacherVO create(AdminTeacherCreateRequest request) {
    if (request == null) {
      throw new RuntimeException("新增参数不能为空");
    }

    String account = required(request.getAccount(), "老师账号不能为空");
    String password = required(request.getPassword(), "老师密码不能为空");

    if (teacherMapper.selectById(account) != null) {
      throw new RuntimeException("老师账号已存在");
    }

    Teacher teacher = new Teacher();
    teacher.setAccount(account);
    teacher.setPassword(password);
    teacher.setTeacherName(normalize(request.getTeacherName()));
    teacher.setOfficeLocation(normalize(request.getOfficeLocation()));
    teacher.setPhone(normalize(request.getPhone()));
    teacher.setAvatarUrl(null);

    teacherMapper.insert(teacher);
    return toVO(teacher);
  }

  private AdminTeacherVO update(AdminTeacherUpdateRequest request) {
    if (request == null) {
      throw new RuntimeException("修改参数不能为空");
    }

    String account = required(request.getAccount(), "老师账号不能为空");

    Teacher teacher = teacherMapper.selectById(account);
    if (teacher == null) {
      throw new RuntimeException("心理老师不存在");
    }

    String password = normalize(request.getPassword());
    if (password != null) {
      teacher.setPassword(password);
    }

    if (request.getTeacherName() != null) {
      teacher.setTeacherName(normalize(request.getTeacherName()));
    }

    if (request.getOfficeLocation() != null) {
      teacher.setOfficeLocation(normalize(request.getOfficeLocation()));
    }

    if (request.getPhone() != null) {
      teacher.setPhone(normalize(request.getPhone()));
    }

    if (request.getAvatarUrl() != null) {
      teacher.setAvatarUrl(normalize(request.getAvatarUrl()));
    }

    teacherMapper.updateById(teacher);
    return toVO(teacher);
  }

  private AdminTeacherVO toVO(Teacher teacher) {
    AdminTeacherVO vo = new AdminTeacherVO();
    vo.setAccount(teacher.getAccount());
    vo.setTeacherName(teacher.getTeacherName());
    vo.setOfficeLocation(teacher.getOfficeLocation());
    vo.setPhone(teacher.getPhone());
    vo.setAvatarUrl(teacher.getAvatarUrl());
    return vo;
  }

  private String required(String value, String message) {
    String result = normalize(value);
    if (result == null) {
      throw new RuntimeException(message);
    }
    return result;
  }

  private String normalize(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  private AdminTeacherManageResponseMessage successTeacher(
      String requestId, String message, AdminTeacherVO teacherVO) {
    AdminTeacherManageResponseMessage response = new AdminTeacherManageResponseMessage();
    response.setRequestId(requestId);
    response.setSuccess(true);
    response.setMessage(message);
    response.setTeacher(teacherVO);
    return response;
  }

  private AdminTeacherManageResponseMessage successPage(
      String requestId, String message, AdminTeacherPageVO pageVO) {
    AdminTeacherManageResponseMessage response = new AdminTeacherManageResponseMessage();
    response.setRequestId(requestId);
    response.setSuccess(true);
    response.setMessage(message);
    response.setPage(pageVO);
    return response;
  }

  private AdminTeacherManageResponseMessage fail(String requestId, String message) {
    AdminTeacherManageResponseMessage response = new AdminTeacherManageResponseMessage();
    response.setRequestId(requestId);
    response.setSuccess(false);
    response.setMessage(message);
    return response;
  }
}