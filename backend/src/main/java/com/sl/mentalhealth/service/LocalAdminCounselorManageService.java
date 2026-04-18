package com.sl.mentalhealth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sl.mentalhealth.dto.AdminCounselorClassesUpdateRequest;
import com.sl.mentalhealth.dto.AdminCounselorCreateRequest;
import com.sl.mentalhealth.dto.AdminCounselorQueryRequest;
import com.sl.mentalhealth.dto.AdminCounselorUpdateRequest;
import com.sl.mentalhealth.entity.Counselor;
import com.sl.mentalhealth.entity.CounselorClassMapping;
import com.sl.mentalhealth.kafka.message.AdminCounselorManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminCounselorManageResponseMessage;
import com.sl.mentalhealth.mapper.CounselorClassMappingMapper;
import com.sl.mentalhealth.mapper.CounselorMapper;
import com.sl.mentalhealth.vo.AdminCounselorDetailVO;
import com.sl.mentalhealth.vo.AdminCounselorPageVO;
import com.sl.mentalhealth.vo.AdminCounselorVO;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class LocalAdminCounselorManageService {

  private final CounselorMapper counselorMapper;
  private final CounselorClassMappingMapper counselorClassMappingMapper;

  public LocalAdminCounselorManageService(
      CounselorMapper counselorMapper,
      CounselorClassMappingMapper counselorClassMappingMapper) {
    this.counselorMapper = counselorMapper;
    this.counselorClassMappingMapper = counselorClassMappingMapper;
  }

  public AdminCounselorManageResponseMessage handle(
      AdminCounselorManageRequestMessage requestMessage) {
    try {
      if (requestMessage == null || !StringUtils.hasText(requestMessage.getAction())) {
        return fail(null, "请求参数不正确");
      }

      String action = requestMessage.getAction();

      if (AdminCounselorManageRequestMessage.ACTION_QUERY_PAGE.equals(action)) {
        AdminCounselorPageVO pageVO = queryPage(requestMessage.getQueryRequest());
        return successPage(requestMessage.getRequestId(), "查询成功", pageVO);
      }

      if (AdminCounselorManageRequestMessage.ACTION_DETAIL.equals(action)) {
        AdminCounselorDetailVO detailVO = detail(requestMessage.getAccount());
        return successDetail(requestMessage.getRequestId(), "查询成功", detailVO);
      }

      if (AdminCounselorManageRequestMessage.ACTION_CREATE.equals(action)) {
        AdminCounselorDetailVO detailVO = create(requestMessage.getCreateRequest());
        return successDetail(requestMessage.getRequestId(), "新增成功", detailVO);
      }

      if (AdminCounselorManageRequestMessage.ACTION_UPDATE.equals(action)) {
        AdminCounselorDetailVO detailVO = update(requestMessage.getUpdateRequest());
        return successDetail(requestMessage.getRequestId(), "修改成功", detailVO);
      }

      if (AdminCounselorManageRequestMessage.ACTION_UPDATE_CLASSES.equals(action)) {
        AdminCounselorDetailVO detailVO = updateClasses(requestMessage.getClassesUpdateRequest());
        return successDetail(requestMessage.getRequestId(), "班级更新成功", detailVO);
      }

      return fail(requestMessage.getRequestId(), "不支持的操作类型");
    } catch (Exception e) {
      return fail(
          requestMessage == null ? null : requestMessage.getRequestId(),
          e.getMessage() == null ? "操作失败" : e.getMessage()
      );
    }
  }

  private AdminCounselorPageVO queryPage(AdminCounselorQueryRequest request) {
    AdminCounselorQueryRequest queryRequest =
        request == null ? new AdminCounselorQueryRequest() : request;

    int pageNum = queryRequest.getPageNum() == null || queryRequest.getPageNum() < 1
        ? 1 : queryRequest.getPageNum();
    int pageSize = queryRequest.getPageSize() == null || queryRequest.getPageSize() < 1
        ? 10 : queryRequest.getPageSize();

    LambdaQueryWrapper<Counselor> queryWrapper = Wrappers.lambdaQuery();

    if (StringUtils.hasText(queryRequest.getAccount())) {
      queryWrapper.like(Counselor::getAccount, queryRequest.getAccount().trim());
    }
    if (StringUtils.hasText(queryRequest.getName())) {
      queryWrapper.like(Counselor::getName, queryRequest.getName().trim());
    }
    if (StringUtils.hasText(queryRequest.getCollege())) {
      queryWrapper.like(Counselor::getCollege, queryRequest.getCollege().trim());
    }
    if (StringUtils.hasText(queryRequest.getGrade())) {
      queryWrapper.like(Counselor::getGrade, queryRequest.getGrade().trim());
    }
    if (StringUtils.hasText(queryRequest.getPhone())) {
      queryWrapper.like(Counselor::getPhone, queryRequest.getPhone().trim());
    }

    queryWrapper.orderByAsc(Counselor::getAccount);

    Page<Counselor> page = new Page<>(pageNum, pageSize);
    Page<Counselor> counselorPage = counselorMapper.selectPage(page, queryWrapper);

    List<AdminCounselorVO> list = new ArrayList<>();
    for (Counselor counselor : counselorPage.getRecords()) {
      list.add(toVO(counselor));
    }

    AdminCounselorPageVO pageVO = new AdminCounselorPageVO();
    pageVO.setTotal(counselorPage.getTotal());
    pageVO.setPageNum(pageNum);
    pageVO.setPageSize(pageSize);
    pageVO.setList(list);
    return pageVO;
  }

  private AdminCounselorDetailVO detail(String account) {
    String counselorAccount = required(account, "辅导员账号不能为空");
    Counselor counselor = counselorMapper.selectById(counselorAccount);
    if (counselor == null) {
      throw new RuntimeException("辅导员不存在");
    }
    return toDetailVO(counselor);
  }

  private AdminCounselorDetailVO create(AdminCounselorCreateRequest request) {
    if (request == null) {
      throw new RuntimeException("新增参数不能为空");
    }

    String account = required(request.getAccount(), "辅导员账号不能为空");
    String name = required(request.getName(), "辅导员姓名不能为空");
    String password = required(request.getPassword(), "辅导员密码不能为空");
    String college = required(request.getCollege(), "学院不能为空");
    String grade = required(request.getGrade(), "年级不能为空");
    String phone = required(request.getPhone(), "电话不能为空");

    if (counselorMapper.selectById(account) != null) {
      throw new RuntimeException("辅导员账号已存在");
    }

    Counselor counselor = new Counselor();
    counselor.setAccount(account);
    counselor.setName(name);
    counselor.setPassword(password);
    counselor.setCollege(college);
    counselor.setGrade(grade);
    counselor.setPhone(phone);
    counselor.setAvatarUrl(null);

    counselorMapper.insert(counselor);

    replaceClasses(account, request.getClassList());
    return detail(account);
  }

  private AdminCounselorDetailVO update(AdminCounselorUpdateRequest request) {
    if (request == null) {
      throw new RuntimeException("修改参数不能为空");
    }

    String account = required(request.getAccount(), "辅导员账号不能为空");

    Counselor counselor = counselorMapper.selectById(account);
    if (counselor == null) {
      throw new RuntimeException("辅导员不存在");
    }

    if (request.getName() != null) {
      counselor.setName(normalize(request.getName()));
    }
    if (request.getCollege() != null) {
      counselor.setCollege(normalize(request.getCollege()));
    }
    if (request.getGrade() != null) {
      counselor.setGrade(normalize(request.getGrade()));
    }
    if (request.getPhone() != null) {
      counselor.setPhone(normalize(request.getPhone()));
    }

    String password = normalize(request.getPassword());
    if (password != null) {
      counselor.setPassword(password);
    }

    counselorMapper.updateById(counselor);
    return detail(account);
  }

  private AdminCounselorDetailVO updateClasses(AdminCounselorClassesUpdateRequest request) {
    if (request == null) {
      throw new RuntimeException("班级修改参数不能为空");
    }

    String account = required(request.getAccount(), "辅导员账号不能为空");

    Counselor counselor = counselorMapper.selectById(account);
    if (counselor == null) {
      throw new RuntimeException("辅导员不存在");
    }

    replaceClasses(account, request.getClassList());
    return detail(account);
  }

  private void replaceClasses(String counselorAccount, List<String> classList) {
    List<String> normalizedClassList = normalizeClassList(classList);

    for (String className : normalizedClassList) {
      CounselorClassMapping existing = findFirstByClassName(className);
      if (existing != null
          && !counselorAccount.equals(existing.getCounselorAccount())) {
        throw new RuntimeException("班级【" + className + "】已分配给其他辅导员");
      }
    }

    counselorClassMappingMapper.delete(
        Wrappers.<CounselorClassMapping>lambdaQuery()
            .eq(CounselorClassMapping::getCounselorAccount, counselorAccount)
    );

    for (String className : normalizedClassList) {
      CounselorClassMapping mapping = new CounselorClassMapping();
      mapping.setCounselorAccount(counselorAccount);
      mapping.setClassName(className);
      counselorClassMappingMapper.insert(mapping);
    }
  }

  private CounselorClassMapping findFirstByClassName(String className) {
    List<CounselorClassMapping> list = counselorClassMappingMapper.selectList(
        Wrappers.<CounselorClassMapping>lambdaQuery()
            .eq(CounselorClassMapping::getClassName, className)
            .last("LIMIT 1")
    );
    return list.isEmpty() ? null : list.get(0);
  }

  private List<CounselorClassMapping> listByCounselorAccount(String counselorAccount) {
    return counselorClassMappingMapper.selectList(
        Wrappers.<CounselorClassMapping>lambdaQuery()
            .eq(CounselorClassMapping::getCounselorAccount, counselorAccount)
            .orderByAsc(CounselorClassMapping::getClassName)
    );
  }

  private List<String> normalizeClassList(List<String> classList) {
    if (classList == null || classList.isEmpty()) {
      return new ArrayList<>();
    }

    Set<String> set = new LinkedHashSet<>();
    for (String item : classList) {
      String className = normalize(item);
      if (className != null) {
        set.add(className);
      }
    }
    return new ArrayList<>(set);
  }

  private AdminCounselorVO toVO(Counselor counselor) {
    AdminCounselorVO vo = new AdminCounselorVO();
    vo.setAccount(counselor.getAccount());
    vo.setName(counselor.getName());
    vo.setCollege(counselor.getCollege());
    vo.setGrade(counselor.getGrade());
    vo.setPhone(counselor.getPhone());
    return vo;
  }

  private AdminCounselorDetailVO toDetailVO(Counselor counselor) {
    AdminCounselorDetailVO vo = new AdminCounselorDetailVO();
    vo.setAccount(counselor.getAccount());
    vo.setName(counselor.getName());
    vo.setCollege(counselor.getCollege());
    vo.setGrade(counselor.getGrade());
    vo.setPhone(counselor.getPhone());
    vo.setAvatarUrl(counselor.getAvatarUrl());

    List<CounselorClassMapping> mappings = listByCounselorAccount(counselor.getAccount());

    List<String> classList = new ArrayList<>();
    for (CounselorClassMapping mapping : mappings) {
      classList.add(mapping.getClassName());
    }
    vo.setClassList(classList);
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

  private AdminCounselorManageResponseMessage successPage(
      String requestId, String message, AdminCounselorPageVO pageVO) {
    AdminCounselorManageResponseMessage response = new AdminCounselorManageResponseMessage();
    response.setRequestId(requestId);
    response.setSuccess(true);
    response.setMessage(message);
    response.setPage(pageVO);
    return response;
  }

  private AdminCounselorManageResponseMessage successDetail(
      String requestId, String message, AdminCounselorDetailVO detailVO) {
    AdminCounselorManageResponseMessage response = new AdminCounselorManageResponseMessage();
    response.setRequestId(requestId);
    response.setSuccess(true);
    response.setMessage(message);
    response.setDetail(detailVO);
    return response;
  }

  private AdminCounselorManageResponseMessage fail(String requestId, String message) {
    AdminCounselorManageResponseMessage response = new AdminCounselorManageResponseMessage();
    response.setRequestId(requestId);
    response.setSuccess(false);
    response.setMessage(message);
    return response;
  }
}