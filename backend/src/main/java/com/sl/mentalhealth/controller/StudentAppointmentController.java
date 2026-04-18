package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.config.UserContext;
import com.sl.mentalhealth.dto.AppointmentCancelRequest;
import com.sl.mentalhealth.dto.AppointmentCreateRequest;
import com.sl.mentalhealth.service.AppointmentGatewayService;
import com.sl.mentalhealth.vo.AppointmentVO;
import com.sl.mentalhealth.vo.AvailableAppointmentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/appointment")
@RequiredArgsConstructor
public class StudentAppointmentController {

  private final AppointmentGatewayService appointmentGatewayService;

  @GetMapping("/available")
  public Result<List<AvailableAppointmentVO>> available(@RequestParam(required = false) String date) {
    List<AvailableAppointmentVO> data = appointmentGatewayService.studentAvailable(date);
    return Result.success("查询成功", data);
  }

  @PostMapping("/create")
  public Result<Long> create(@RequestBody AppointmentCreateRequest request) {
    request.setStudentId(UserContext.getUsername());
    Long appointmentId = appointmentGatewayService.studentCreate(request);
    return Result.success("预约提交成功", appointmentId);
  }

  @GetMapping("/my")
  public Result<List<AppointmentVO>> my() {
    String studentId = UserContext.getUsername();
    List<AppointmentVO> data = appointmentGatewayService.studentMy(studentId);
    return Result.success("查询成功", data);
  }

  @PostMapping("/cancel")
  public Result<Void> cancel(@RequestBody AppointmentCancelRequest request) {
    request.setStudentId(UserContext.getUsername());
    appointmentGatewayService.studentCancel(request);
    return Result.success("取消成功", null);
  }
}