package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.AdminCounselorClassesUpdateRequest;
import com.sl.mentalhealth.dto.AdminCounselorCreateRequest;
import com.sl.mentalhealth.dto.AdminCounselorQueryRequest;
import com.sl.mentalhealth.dto.AdminCounselorUpdateRequest;
import com.sl.mentalhealth.service.AdminCounselorManageGatewayService;
import com.sl.mentalhealth.vo.AdminCounselorDetailVO;
import com.sl.mentalhealth.vo.AdminCounselorPageVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/counselor")
public class AdminCounselorManageController {

  private final AdminCounselorManageGatewayService gatewayService;

  public AdminCounselorManageController(AdminCounselorManageGatewayService gatewayService) {
    this.gatewayService = gatewayService;
  }

  @PostMapping("/page")
  public Result<AdminCounselorPageVO> page(
      @RequestBody(required = false) AdminCounselorQueryRequest request) {
    AdminCounselorQueryRequest realRequest =
        request == null ? new AdminCounselorQueryRequest() : request;
    return Result.success("查询成功", gatewayService.queryPage(realRequest));
  }

  @GetMapping("/detail")
  public Result<AdminCounselorDetailVO> detail(@RequestParam("account") String account) {
    return Result.success("查询成功", gatewayService.detail(account));
  }

  @PostMapping("/create")
  public Result<AdminCounselorDetailVO> create(
      @RequestBody AdminCounselorCreateRequest request) {
    return Result.success("新增成功", gatewayService.create(request));
  }

  @PostMapping("/update")
  public Result<AdminCounselorDetailVO> update(
      @RequestBody AdminCounselorUpdateRequest request) {
    return Result.success("修改成功", gatewayService.update(request));
  }

  @PostMapping("/update-classes")
  public Result<AdminCounselorDetailVO> updateClasses(
      @RequestBody AdminCounselorClassesUpdateRequest request) {
    return Result.success("班级更新成功", gatewayService.updateClasses(request));
  }
}