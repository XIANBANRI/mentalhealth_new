package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorProfileResponseMessage;
import com.sl.mentalhealth.service.LocalCounselorProfileService;
import com.sl.mentalhealth.vo.CounselorProfileResponseVO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CounselorProfileRequestConsumer {

  private final LocalCounselorProfileService localCounselorProfileService;
  private final CounselorProfileResponseProducer counselorProfileResponseProducer;

  public CounselorProfileRequestConsumer(
      LocalCounselorProfileService localCounselorProfileService,
      CounselorProfileResponseProducer counselorProfileResponseProducer) {
    this.localCounselorProfileService = localCounselorProfileService;
    this.counselorProfileResponseProducer = counselorProfileResponseProducer;
  }

  @KafkaListener(
      topics = KafkaTopics.COUNSELOR_PROFILE_REQUEST,
      groupId = "mental-health-counselor-profile-request",
      containerFactory = "counselorProfileRequestKafkaListenerContainerFactory"
  )
  public void consume(CounselorProfileRequestMessage request) {
    CounselorProfileResponseMessage response;

    try {
      CounselorProfileResponseVO data;

      if (CounselorProfileRequestMessage.ACTION_UPDATE_AVATAR.equals(request.getAction())) {
        data = localCounselorProfileService.updateAvatar(
            request.getAccount(), request.getAvatarUrl());

        if (data == null) {
          response = new CounselorProfileResponseMessage(
              request.getCorrelationId(),
              false,
              "未找到辅导员信息",
              null
          );
        } else {
          response = new CounselorProfileResponseMessage(
              request.getCorrelationId(),
              true,
              "头像上传成功",
              data
          );
        }
      } else {
        data = localCounselorProfileService.getProfile(request.getAccount());

        if (data == null) {
          response = new CounselorProfileResponseMessage(
              request.getCorrelationId(),
              false,
              "未找到辅导员信息",
              null
          );
        } else {
          response = new CounselorProfileResponseMessage(
              request.getCorrelationId(),
              true,
              "查询成功",
              data
          );
        }
      }
    } catch (Exception e) {
      response = new CounselorProfileResponseMessage(
          request.getCorrelationId(),
          false,
          "处理辅导员信息失败：" + e.getMessage(),
          null
      );
    }

    counselorProfileResponseProducer.send(response);
  }
}