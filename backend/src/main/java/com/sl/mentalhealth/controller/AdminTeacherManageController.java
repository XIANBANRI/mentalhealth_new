package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.AdminTeacherCreateRequest;
import com.sl.mentalhealth.dto.AdminTeacherQueryRequest;
import com.sl.mentalhealth.dto.AdminTeacherUpdateRequest;
import com.sl.mentalhealth.service.AdminTeacherManageGatewayService;
import com.sl.mentalhealth.vo.AdminTeacherPageVO;
import com.sl.mentalhealth.vo.AdminTeacherVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/teacher")
public class AdminTeacherManageController {

  private final AdminTeacherManageGatewayService gatewayService;

  public AdminTeacherManageController(AdminTeacherManageGatewayService gatewayService) {
    this.gatewayService = gatewayService;
  }

  @PostMapping("/page")
  public Result<AdminTeacherPageVO> page(@RequestBody(required = false) AdminTeacherQueryRequest request) {
    AdminTeacherQueryRequest realRequest = request == null ? new AdminTeacherQueryRequest() : request;
    return Result.success("查询成功", gatewayService.queryPage(realRequest));
  }

  @GetMapping("/detail")
  public Result<AdminTeacherVO> detail(@RequestParam("account") String account) {
    return Result.success("查询成功", gatewayService.detail(account));
  }

  @PostMapping("/create")
  public Result<AdminTeacherVO> create(@RequestBody AdminTeacherCreateRequest request) {
    return Result.success("新增成功", gatewayService.create(request));
  }

  @PostMapping("/update")
  public Result<AdminTeacherVO> update(@RequestBody AdminTeacherUpdateRequest request) {
    return Result.success("修改成功", gatewayService.update(request));
  }
}