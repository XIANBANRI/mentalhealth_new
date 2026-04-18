package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.AdminProfileRequestMessage;
import com.sl.mentalhealth.kafka.message.AdminProfileResponseMessage;
import com.sl.mentalhealth.service.LocalAdminProfileService;
import com.sl.mentalhealth.vo.AdminProfileResponseVO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AdminProfileRequestConsumer {

  private final LocalAdminProfileService localAdminProfileService;
  private final AdminProfileResponseProducer adminProfileResponseProducer;

  public AdminProfileRequestConsumer(
      LocalAdminProfileService localAdminProfileService,
      AdminProfileResponseProducer adminProfileResponseProducer) {
    this.localAdminProfileService = localAdminProfileService;
    this.adminProfileResponseProducer = adminProfileResponseProducer;
  }

  @KafkaListener(
      topics = KafkaTopics.ADMIN_PROFILE_REQUEST,
      containerFactory = "adminProfileRequestKafkaListenerContainerFactory"
  )
  public void consume(AdminProfileRequestMessage message) {
    if (message == null) {
      return;
    }

    try {
      AdminProfileResponseVO data;

      if (AdminProfileRequestMessage.ACTION_UPDATE_AVATAR.equals(message.getAction())) {
        data = localAdminProfileService.updateAvatar(
            message.getAccount(), message.getAvatarUrl());

        AdminProfileResponseMessage responseMessage = new AdminProfileResponseMessage();
        responseMessage.setRequestId(message.getRequestId());
        responseMessage.setSuccess(true);
        responseMessage.setMessage("头像上传成功");
        responseMessage.setData(data);

        adminProfileResponseProducer.send(responseMessage);
      } else {
        data = localAdminProfileService.getAdminProfile(message.getAccount());

        AdminProfileResponseMessage responseMessage = new AdminProfileResponseMessage();
        responseMessage.setRequestId(message.getRequestId());
        responseMessage.setSuccess(true);
        responseMessage.setMessage("查询管理员信息成功");
        responseMessage.setData(data);

        adminProfileResponseProducer.send(responseMessage);
      }
    } catch (Exception e) {
      AdminProfileResponseMessage responseMessage = new AdminProfileResponseMessage();
      responseMessage.setRequestId(message.getRequestId());
      responseMessage.setSuccess(false);
      responseMessage.setMessage(e.getMessage());
      responseMessage.setData(null);

      adminProfileResponseProducer.send(responseMessage);
    }
  }
}