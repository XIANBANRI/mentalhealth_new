package com.sl.mentalhealth.kafka;

import com.sl.mentalhealth.kafka.message.CounselorTrendReportResponseMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorTrendReportResponseProducerTest {

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  @InjectMocks
  private CounselorTrendReportResponseProducer producer;

  @Test
  void send_success() {
    CounselorTrendReportResponseMessage message = mock(CounselorTrendReportResponseMessage.class);
    when(message.getRequestId()).thenReturn("req-001");

    producer.send(message);

    verify(kafkaTemplate, times(1)).send(
        KafkaTopics.COUNSELOR_TREND_REPORT_RESPONSE,
        "req-001",
        message
    );
  }
}