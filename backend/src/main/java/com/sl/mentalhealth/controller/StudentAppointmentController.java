package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.AppointmentCancelRequest;
import com.sl.mentalhealth.dto.AppointmentCreateRequest;
import com.sl.mentalhealth.dto.TeacherAppointmentAuditRequest;
import com.sl.mentalhealth.service.AppointmentGatewayService;
import com.sl.mentalhealth.vo.AppointmentVO;
import com.sl.mentalhealth.vo.AvailableAppointmentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class StudentAppointmentController {

  private final AppointmentGatewayService appointmentGatewayService;

  // ==================== 学生端 ====================

  @GetMapping("/available")
  public Result<List<AvailableAppointmentVO>> available(@RequestParam(required = false) String date) {
    List<AvailableAppointmentVO> data = appointmentGatewayService.studentAvailable(date);
    return Result.success("查询成功", data);
  }

  @PostMapping("/create")
  public Result<Long> create(@RequestBody AppointmentCreateRequest request) {
    Long appointmentId = appointmentGatewayService.studentCreate(request);
    return Result.success("预约提交成功", appointmentId);
  }

  @GetMapping("/my")
  public Result<List<AppointmentVO>> my(@RequestParam String studentId) {
    List<AppointmentVO> data = appointmentGatewayService.studentMy(studentId);
    return Result.success("查询成功", data);
  }

  @PostMapping("/cancel")
  public Result<Void> cancel(@RequestBody AppointmentCancelRequest request) {
    appointmentGatewayService.studentCancel(request);
    return Result.success("取消成功", null);
  }

  // ==================== 老师端记录管理 ====================

  @GetMapping("/teacher/list")
  public Result<List<AppointmentVO>> teacherList(@RequestParam String teacherAccount,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String date) {
    List<AppointmentVO> data = appointmentGatewayService.teacherList(teacherAccount, status, date);
    return Result.success("查询成功", data);
  }

  @PostMapping("/teacher/approve")
  public Result<Void> teacherApprove(@RequestBody TeacherAppointmentAuditRequest request) {
    appointmentGatewayService.teacherApprove(request);
    return Result.success("通过成功", null);
  }

  @PostMapping("/teacher/reject")
  public Result<Void> teacherReject(@RequestBody TeacherAppointmentAuditRequest request) {
    appointmentGatewayService.teacherReject(request);
    return Result.success("拒绝成功", null);
  }

  @PostMapping("/teacher/complete")
  public Result<Void> teacherComplete(@RequestBody TeacherAppointmentAuditRequest request) {
    appointmentGatewayService.teacherComplete(request);
    return Result.success("完成成功", null);
  }
}