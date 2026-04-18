package com.sl.mentalhealth.service;

import com.sl.mentalhealth.dto.TeacherScheduleDeleteRequest;
import com.sl.mentalhealth.dto.TeacherScheduleQueryRequest;
import com.sl.mentalhealth.dto.TeacherScheduleSaveRequest;
import com.sl.mentalhealth.kafka.TeacherScheduleRequestProducer;
import com.sl.mentalhealth.kafka.message.TeacherScheduleResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherScheduleGatewayService {

  private final TeacherScheduleRequestProducer teacherScheduleRequestProducer;
  private final PendingTeacherScheduleService pendingTeacherScheduleService;

  public TeacherScheduleResponseMessage query(TeacherScheduleQueryRequest request) {
    String requestId = teacherScheduleRequestProducer.sendQuery(request);
    return pendingTeacherScheduleService.waitResponse(requestId, 8);
  }

  public TeacherScheduleResponseMessage add(TeacherScheduleSaveRequest request) {
    String requestId = teacherScheduleRequestProducer.sendAdd(request);
    return pendingTeacherScheduleService.waitResponse(requestId, 8);
  }

  public TeacherScheduleResponseMessage update(TeacherScheduleSaveRequest request) {
    String requestId = teacherScheduleRequestProducer.sendUpdate(request);
    return pendingTeacherScheduleService.waitResponse(requestId, 8);
  }

  public TeacherScheduleResponseMessage delete(TeacherScheduleDeleteRequest request) {
    String requestId = teacherScheduleRequestProducer.sendDelete(request);
    return pendingTeacherScheduleService.waitResponse(requestId, 8);
  }
}