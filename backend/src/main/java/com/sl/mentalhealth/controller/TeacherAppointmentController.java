package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.TeacherAppointmentQueryRequest;
import com.sl.mentalhealth.dto.TeacherAppointmentUpdateStatusRequest;
import com.sl.mentalhealth.dto.TeacherAssessmentRecordQueryRequest;
import com.sl.mentalhealth.kafka.message.TeacherAppointmentResponseMessage;
import com.sl.mentalhealth.service.TeacherAppointmentGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher/appointment")
@RequiredArgsConstructor
@CrossOrigin
public class TeacherAppointmentController {

  private final TeacherAppointmentGatewayService teacherAppointmentGatewayService;

  @PostMapping("/query")
  public Result<?> query(@RequestBody TeacherAppointmentQueryRequest request) {
    TeacherAppointmentResponseMessage response = teacherAppointmentGatewayService.query(request);
    if (response.isSuccess()) {
      return Result.success(response.getMessage(), response.getAppointmentList());
    }
    return Result.error(response.getMessage());
  }

  @PostMapping("/record")
  public Result<?> record(@RequestBody TeacherAppointmentQueryRequest request) {
    TeacherAppointmentResponseMessage response = teacherAppointmentGatewayService.record(request);
    if (response.isSuccess()) {
      return Result.success(response.getMessage(), response.getAppointmentList());
    }
    return Result.error(response.getMessage());
  }

  @PostMapping("/updateStatus")
  public Result<?> updateStatus(@RequestBody TeacherAppointmentUpdateStatusRequest request) {
    TeacherAppointmentResponseMessage response = teacherAppointmentGatewayService.updateStatus(request);
    if (response.isSuccess()) {
      return Result.success(response.getMessage(), response.getAppointmentData());
    }
    return Result.error(response.getMessage());
  }

  @PostMapping("/assessmentRecord")
  public Result<?> assessmentRecord(@RequestBody TeacherAssessmentRecordQueryRequest request) {
    TeacherAppointmentResponseMessage response = teacherAppointmentGatewayService.assessmentRecord(request);
    if (response.isSuccess()) {
      return Result.success(response.getMessage(), response.getAssessmentRecordList());
    }
    return Result.error(response.getMessage());
  }
}