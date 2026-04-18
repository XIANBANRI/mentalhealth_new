package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.TeacherScheduleDeleteRequest;
import com.sl.mentalhealth.dto.TeacherScheduleQueryRequest;
import com.sl.mentalhealth.dto.TeacherScheduleSaveRequest;
import com.sl.mentalhealth.kafka.message.TeacherScheduleResponseMessage;
import com.sl.mentalhealth.service.TeacherScheduleGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher/schedule")
@RequiredArgsConstructor
@CrossOrigin
public class TeacherScheduleController {

  private final TeacherScheduleGatewayService teacherScheduleGatewayService;

  @PostMapping("/query")
  public Result<?> query(@RequestBody TeacherScheduleQueryRequest request) {
    TeacherScheduleResponseMessage response = teacherScheduleGatewayService.query(request);
    if (response.isSuccess()) {
      return Result.success(response.getMessage(), response.getList());
    }
    return Result.error(response.getMessage());
  }

  @PostMapping("/add")
  public Result<?> add(@RequestBody TeacherScheduleSaveRequest request) {
    TeacherScheduleResponseMessage response = teacherScheduleGatewayService.add(request);
    if (response.isSuccess()) {
      return Result.success(response.getMessage(), response.getData());
    }
    return Result.error(response.getMessage());
  }

  @PostMapping("/update")
  public Result<?> update(@RequestBody TeacherScheduleSaveRequest request) {
    TeacherScheduleResponseMessage response = teacherScheduleGatewayService.update(request);
    if (response.isSuccess()) {
      return Result.success(response.getMessage(), response.getData());
    }
    return Result.error(response.getMessage());
  }

  @PostMapping("/delete")
  public Result<?> delete(@RequestBody TeacherScheduleDeleteRequest request) {
    TeacherScheduleResponseMessage response = teacherScheduleGatewayService.delete(request);
    if (response.isSuccess()) {
      return Result.success(response.getMessage(), null);
    }
    return Result.error(response.getMessage());
  }
}