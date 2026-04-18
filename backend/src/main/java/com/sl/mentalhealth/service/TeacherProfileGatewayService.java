package com.sl.mentalhealth.service;

import com.sl.mentalhealth.kafka.TeacherProfileRequestProducer;
import com.sl.mentalhealth.kafka.message.TeacherProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.TeacherProfileResponseMessage;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TeacherProfileGatewayService {

  private final TeacherProfileRequestProducer teacherProfileRequestProducer;
  private final PendingTeacherProfileService pendingTeacherProfileService;

  @Value("${app.kafka.teacher-profile-timeout-seconds:8}")
  private long timeoutSeconds;

  public TeacherProfileResponseMessage getTeacherProfile(String teacherAccount) {
    if (!StringUtils.hasText(teacherAccount)) {
      return pendingTeacherProfileService.buildErrorResponse(null, "老师账号不能为空");
    }

    TeacherProfileRequestMessage message = new TeacherProfileRequestMessage();
    message.setRequestId(generateRequestId());
    message.setAction(TeacherProfileRequestMessage.ACTION_QUERY_PROFILE);
    message.setTeacherAccount(teacherAccount.trim());

    return sendAndWait(message);
  }

  public TeacherProfileResponseMessage updateAvatar(String teacherAccount, String avatarUrl) {
    if (!StringUtils.hasText(teacherAccount)) {
      return pendingTeacherProfileService.buildErrorResponse(null, "老师账号不能为空");
    }
    if (!StringUtils.hasText(avatarUrl)) {
      return pendingTeacherProfileService.buildErrorResponse(null, "头像地址不能为空");
    }

    TeacherProfileRequestMessage message = new TeacherProfileRequestMessage();
    message.setRequestId(generateRequestId());
    message.setAction(TeacherProfileRequestMessage.ACTION_UPDATE_AVATAR);
    message.setTeacherAccount(teacherAccount.trim());
    message.setAvatarUrl(avatarUrl.trim());

    return sendAndWait(message);
  }

  private TeacherProfileResponseMessage sendAndWait(TeacherProfileRequestMessage message) {
    CompletableFuture<TeacherProfileResponseMessage> future =
        pendingTeacherProfileService.create(message.getRequestId());

    try {
      teacherProfileRequestProducer.send(message);
      return future.get(timeoutSeconds, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      return pendingTeacherProfileService.buildTimeoutResponse(message.getRequestId());
    } catch (Exception e) {
      return pendingTeacherProfileService.buildErrorResponse(
          message.getRequestId(), "老师信息处理失败");
    } finally {
      pendingTeacherProfileService.remove(message.getRequestId());
    }
  }

  private String generateRequestId() {
    return UUID.randomUUID().toString().replace("-", "");
  }
}