package com.sl.mentalhealth.service;

import com.sl.mentalhealth.dto.StudentProfileRequest;
import com.sl.mentalhealth.kafka.StudentProfileRequestProducer;
import com.sl.mentalhealth.kafka.message.StudentProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.StudentProfileResponseMessage;
import com.sl.mentalhealth.vo.StudentProfileResponseVO;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StudentProfileGatewayService {

  private final StudentProfileRequestProducer studentProfileRequestProducer;
  private final PendingStudentProfileService pendingStudentProfileService;

  @Value("${app.kafka.student-profile-timeout-seconds:10}")
  private long timeoutSeconds;

  public StudentProfileGatewayService(
      StudentProfileRequestProducer studentProfileRequestProducer,
      PendingStudentProfileService pendingStudentProfileService) {
    this.studentProfileRequestProducer = studentProfileRequestProducer;
    this.pendingStudentProfileService = pendingStudentProfileService;
  }

  public StudentProfileResponseVO queryProfile(StudentProfileRequest request) {
    if (request == null || request.getStudentId() == null || request.getStudentId().trim().isEmpty()) {
      throw new RuntimeException("学号不能为空");
    }

    StudentProfileRequestMessage message = new StudentProfileRequestMessage();
    message.setRequestId(generateRequestId());
    message.setAction(StudentProfileRequestMessage.ACTION_QUERY_PROFILE);
    message.setStudentId(request.getStudentId().trim());

    return sendAndWait(message);
  }

  public StudentProfileResponseVO updateAvatar(String studentId, String avatarUrl) {
    if (studentId == null || studentId.trim().isEmpty()) {
      throw new RuntimeException("学号不能为空");
    }
    if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
      throw new RuntimeException("头像地址不能为空");
    }

    StudentProfileRequestMessage message = new StudentProfileRequestMessage();
    message.setRequestId(generateRequestId());
    message.setAction(StudentProfileRequestMessage.ACTION_UPDATE_AVATAR);
    message.setStudentId(studentId.trim());
    message.setAvatarUrl(avatarUrl.trim());

    return sendAndWait(message);
  }

  private StudentProfileResponseVO sendAndWait(StudentProfileRequestMessage message) {
    CompletableFuture<StudentProfileResponseMessage> future =
        pendingStudentProfileService.create(message.getRequestId());

    try {
      studentProfileRequestProducer.send(message);

      StudentProfileResponseMessage response = future.get(timeoutSeconds, TimeUnit.SECONDS);

      if (!Boolean.TRUE.equals(response.getSuccess())) {
        throw new RuntimeException(
            response.getMessage() == null ? "学生档案服务处理失败" : response.getMessage()
        );
      }

      return new StudentProfileResponseVO(
          response.getStudentId(),
          response.getName(),
          response.getClassName(),
          response.getCollege(),
          response.getGrade(),
          response.getPhone(),
          response.getAvatarUrl(),
          response.getCounselorName(),
          response.getCounselorPhone()
      );
    } catch (TimeoutException e) {
      throw new RuntimeException("学生档案服务响应超时");
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("学生档案服务调用失败", e);
    } finally {
      pendingStudentProfileService.remove(message.getRequestId());
    }
  }

  private String generateRequestId() {
    return UUID.randomUUID().toString().replace("-", "");
  }
}