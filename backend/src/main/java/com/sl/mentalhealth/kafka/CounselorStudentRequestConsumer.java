package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorStudentRequestMessage;
import com.sl.mentalhealth.kafka.message.CounselorStudentResponseMessage;
import com.sl.mentalhealth.service.LocalCounselorStudentService;
import com.sl.mentalhealth.vo.CounselorStudentDetailVO;
import com.sl.mentalhealth.vo.CounselorStudentPageVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CounselorStudentRequestConsumer {

  private final LocalCounselorStudentService localCounselorStudentService;
  private final CounselorStudentResponseProducer counselorStudentResponseProducer;

  @KafkaListener(
      topics = KafkaTopics.COUNSELOR_STUDENT_REQUEST,
      groupId = "mh-counselor-student-request-group",
      containerFactory = "counselorStudentRequestKafkaListenerContainerFactory"
  )
  public void consume(CounselorStudentRequestMessage message) {
    CounselorStudentResponseMessage response;

    try {
      if ("LIST_CLASSES".equalsIgnoreCase(message.getAction())) {
        List<String> classOptions =
            localCounselorStudentService.listManagedClasses(message.getCounselorAccount());

        response = CounselorStudentResponseMessage.builder()
            .requestId(message.getRequestId())
            .success(true)
            .message("查询班级列表成功")
            .classOptions(classOptions)
            .build();

      } else if ("LIST".equalsIgnoreCase(message.getAction())) {
        CounselorStudentPageVO pageVO = localCounselorStudentService.listStudents(
            message.getCounselorAccount(),
            message.getClassName(),
            message.getKeyword(),
            message.getPageNum(),
            message.getPageSize()
        );

        response = CounselorStudentResponseMessage.builder()
            .requestId(message.getRequestId())
            .success(true)
            .message("查询成功")
            .pageData(pageVO)
            .build();

      } else if ("DETAIL".equalsIgnoreCase(message.getAction())) {
        CounselorStudentDetailVO detailVO = localCounselorStudentService.getStudentDetail(
            message.getCounselorAccount(),
            message.getStudentId()
        );

        response = CounselorStudentResponseMessage.builder()
            .requestId(message.getRequestId())
            .success(true)
            .message("查询成功")
            .detailData(detailVO)
            .build();

      } else {
        response = CounselorStudentResponseMessage.builder()
            .requestId(message.getRequestId())
            .success(false)
            .message("不支持的操作类型：" + message.getAction())
            .build();
      }
    } catch (Exception e) {
      log.error("处理辅导员学生查询请求失败, requestId={}", message.getRequestId(), e);
      response = CounselorStudentResponseMessage.builder()
          .requestId(message.getRequestId())
          .success(false)
          .message(e.getMessage())
          .build();
    }

    counselorStudentResponseProducer.send(response);
  }
}