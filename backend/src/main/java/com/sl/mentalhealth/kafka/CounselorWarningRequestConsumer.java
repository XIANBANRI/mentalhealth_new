package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorWarningRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorWarningResponseMessage;
import com.sl.mentalhealth.service.LocalCounselorWarningService;
import com.sl.mentalhealth.vo.CounselorWarningDetailVO;
import com.sl.mentalhealth.vo.CounselorWarningPageVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CounselorWarningRequestConsumer {

  private final LocalCounselorWarningService localCounselorWarningService;
  private final CounselorWarningResponseProducer counselorWarningResponseProducer;

  @KafkaListener(
      topics = KafkaTopics.COUNSELOR_WARNING_REQUEST,
      groupId = "mh-counselor-warning-request-group",
      containerFactory = "counselorWarningRequestKafkaListenerContainerFactory"
  )
  public void consume(CounselorWarningRequestMessage message) {
    CounselorWarningResponseMessage response;

    try {
      if ("LIST_CLASSES".equalsIgnoreCase(message.getAction())) {
        List<String> classOptions =
            localCounselorWarningService.listManagedClasses(message.getCounselorAccount());

        response = CounselorWarningResponseMessage.builder()
            .requestId(message.getRequestId())
            .success(true)
            .message("查询班级列表成功")
            .classOptions(classOptions)
            .build();

      } else if ("LIST".equalsIgnoreCase(message.getAction())) {
        CounselorWarningPageVO pageVO = localCounselorWarningService.listDangerousStudents(
            message.getCounselorAccount(),
            message.getSemester(),
            message.getClassName(),
            message.getPageNum(),
            message.getPageSize()
        );

        response = CounselorWarningResponseMessage.builder()
            .requestId(message.getRequestId())
            .success(true)
            .message("查询预警名单成功")
            .pageData(pageVO)
            .build();

      } else if ("DETAIL".equalsIgnoreCase(message.getAction())) {
        CounselorWarningDetailVO detailVO = localCounselorWarningService.getDangerousStudentDetail(
            message.getCounselorAccount(),
            message.getStudentId(),
            message.getSemester()
        );

        response = CounselorWarningResponseMessage.builder()
            .requestId(message.getRequestId())
            .success(true)
            .message("查询预警详情成功")
            .detailData(detailVO)
            .build();

      } else {
        response = CounselorWarningResponseMessage.builder()
            .requestId(message.getRequestId())
            .success(false)
            .message("不支持的操作类型：" + message.getAction())
            .build();
      }
    } catch (Exception e) {
      log.error("处理辅导员预警请求失败, requestId={}", message.getRequestId(), e);
      response = CounselorWarningResponseMessage.builder()
          .requestId(message.getRequestId())
          .success(false)
          .message(e.getMessage())
          .build();
    }

    counselorWarningResponseProducer.send(response);
  }
}