package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.config.LoginUser;
import com.sl.mentalhealth.config.UserContext;
import com.sl.mentalhealth.dto.AssessmentScaleUpdateRequest;
import com.sl.mentalhealth.kafka.message.AssessmentScaleManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AssessmentScaleManageResponseMessage;
import com.sl.mentalhealth.service.AssessmentScaleExcelParserService;
import com.sl.mentalhealth.service.AssessmentScaleManageGatewayService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAssessmentScaleControllerTest {

  @Mock
  private AssessmentScaleManageGatewayService assessmentScaleManageGatewayService;

  @Mock
  private AssessmentScaleExcelParserService assessmentScaleExcelParserService;

  @InjectMocks
  private AdminAssessmentScaleController controller;

  @AfterEach
  void tearDown() {
    UserContext.clear();
  }

  @Test
  void importScale_success() throws Exception {
    UserContext.set(new LoginUser("admin", "admin"));

    MockMultipartFile questionFile =
        new MockMultipartFile("questionFile", "question.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            new byte[]{1, 2, 3});

    MockMultipartFile ruleFile =
        new MockMultipartFile("ruleFile", "rule.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            new byte[]{4, 5, 6});

    List<?> questionList = Collections.emptyList();
    List<?> ruleList = Collections.emptyList();
    Map<String, Object> data = Collections.emptyMap();

    when(assessmentScaleExcelParserService.parseQuestionExcel(questionFile)).thenReturn((List) questionList);
    when(assessmentScaleExcelParserService.parseRuleExcel(ruleFile)).thenReturn((List) ruleList);

    AssessmentScaleManageResponseMessage response = mock(AssessmentScaleManageResponseMessage.class);
    when(response.getSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("导入成功");
    when(response.getData()).thenReturn(data);

    when(assessmentScaleManageGatewayService.importScale(any(AssessmentScaleManageRequestMessage.class)))
        .thenReturn(response);

    Result<?> result = controller.importScale(
        "SAS",
        "焦虑量表",
        "焦虑",
        "量表描述",
        "admin",
        questionFile,
        ruleFile
    );

    assertNotNull(result);
    assertEquals(200, result.getCode());
    assertEquals("导入成功", result.getMessage());
    assertSame(data, result.getData());

    ArgumentCaptor<AssessmentScaleManageRequestMessage> captor =
        ArgumentCaptor.forClass(AssessmentScaleManageRequestMessage.class);
    verify(assessmentScaleManageGatewayService).importScale(captor.capture());

    AssessmentScaleManageRequestMessage message = captor.getValue();
    assertNotNull(message);
    assertEquals("IMPORT", message.getAction());
    assertEquals("SAS", message.getScaleCode());
    assertEquals("焦虑量表", message.getScaleName());
    assertEquals("焦虑", message.getScaleType());
    assertEquals("量表描述", message.getDescription());
    assertEquals("admin", message.getOperator());
    assertEquals("question.xlsx", message.getQuestionFileName());
    assertEquals("rule.xlsx", message.getRuleFileName());
    assertNotNull(message.getRequestId());

    verify(assessmentScaleExcelParserService).parseQuestionExcel(questionFile);
    verify(assessmentScaleExcelParserService).parseRuleExcel(ruleFile);
  }

  @Test
  void importScale_whenOperatorBlank_shouldUseCurrentAdminFromUserContext() throws Exception {
    UserContext.set(new LoginUser("admin_current", "admin"));

    MockMultipartFile questionFile =
        new MockMultipartFile("questionFile", "question.xlsx", "application/octet-stream", new byte[]{1});
    MockMultipartFile ruleFile =
        new MockMultipartFile("ruleFile", "rule.xlsx", "application/octet-stream", new byte[]{1});

    when(assessmentScaleExcelParserService.parseQuestionExcel(questionFile)).thenReturn(Collections.emptyList());
    when(assessmentScaleExcelParserService.parseRuleExcel(ruleFile)).thenReturn(Collections.emptyList());

    AssessmentScaleManageResponseMessage response = mock(AssessmentScaleManageResponseMessage.class);
    when(response.getSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("导入成功");
    when(response.getData()).thenReturn(Collections.emptyMap());

    when(assessmentScaleManageGatewayService.importScale(any())).thenReturn(response);

    controller.importScale(
        "SAS", "焦虑量表", "焦虑", null, "   ", questionFile, ruleFile
    );

    ArgumentCaptor<AssessmentScaleManageRequestMessage> captor =
        ArgumentCaptor.forClass(AssessmentScaleManageRequestMessage.class);
    verify(assessmentScaleManageGatewayService).importScale(captor.capture());

    assertEquals("admin_current", captor.getValue().getOperator());
  }

  @Test
  void importScale_whenGatewayReturnsFalse_shouldReturnError() throws Exception {
    UserContext.set(new LoginUser("admin", "admin"));

    MockMultipartFile questionFile =
        new MockMultipartFile("questionFile", "question.xlsx", "application/octet-stream", new byte[]{1});
    MockMultipartFile ruleFile =
        new MockMultipartFile("ruleFile", "rule.xlsx", "application/octet-stream", new byte[]{1});

    when(assessmentScaleExcelParserService.parseQuestionExcel(questionFile)).thenReturn(Collections.emptyList());
    when(assessmentScaleExcelParserService.parseRuleExcel(ruleFile)).thenReturn(Collections.emptyList());

    AssessmentScaleManageResponseMessage response = mock(AssessmentScaleManageResponseMessage.class);
    when(response.getSuccess()).thenReturn(false);
    when(response.getMessage()).thenReturn("导入失败");

    when(assessmentScaleManageGatewayService.importScale(any())).thenReturn(response);

    Result<?> result = controller.importScale(
        "SAS", "焦虑量表", "焦虑", null, "admin", questionFile, ruleFile
    );

    assertNotNull(result);
    assertEquals(500, result.getCode());
    assertEquals("导入失败", result.getMessage());
    assertNull(result.getData());
  }

  @Test
  void importScale_whenParserThrows_shouldReturnError() throws Exception {
    UserContext.set(new LoginUser("admin", "admin"));

    MockMultipartFile questionFile =
        new MockMultipartFile("questionFile", "question.xlsx", "application/octet-stream", new byte[]{1});
    MockMultipartFile ruleFile =
        new MockMultipartFile("ruleFile", "rule.xlsx", "application/octet-stream", new byte[]{1});

    when(assessmentScaleExcelParserService.parseQuestionExcel(questionFile))
        .thenThrow(new RuntimeException("题目Excel解析失败"));

    Result<?> result = controller.importScale(
        "SAS", "焦虑量表", "焦虑", null, "admin", questionFile, ruleFile
    );

    assertNotNull(result);
    assertEquals(500, result.getCode());
    assertEquals("题目Excel解析失败", result.getMessage());
    assertNull(result.getData());

    verify(assessmentScaleManageGatewayService, never()).importScale(any());
  }

  @Test
  void list_success() {
    List<?> data = Collections.emptyList();

    AssessmentScaleManageResponseMessage response = mock(AssessmentScaleManageResponseMessage.class);
    when(response.getSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("查询成功");
    when(response.getData()).thenReturn(data);

    when(assessmentScaleManageGatewayService.listAll()).thenReturn(response);

    Result<?> result = controller.list();

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(data, result.getData());
  }

  @Test
  void detail_success() {
    Map<String, Object> data = Collections.emptyMap();

    AssessmentScaleManageResponseMessage response = mock(AssessmentScaleManageResponseMessage.class);
    when(response.getSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("查询成功");
    when(response.getData()).thenReturn(data);

    when(assessmentScaleManageGatewayService.detail(1L)).thenReturn(response);

    Result<?> result = controller.detail(1L);

    assertEquals(200, result.getCode());
    assertEquals("查询成功", result.getMessage());
    assertSame(data, result.getData());
  }

  @Test
  void update_success() {
    AssessmentScaleUpdateRequest request = mock(AssessmentScaleUpdateRequest.class);
    List<?> rules = Collections.emptyList();
    Map<String, Object> data = Collections.emptyMap();

    when(request.getRules()).thenReturn((List) rules);

    AssessmentScaleManageResponseMessage response = mock(AssessmentScaleManageResponseMessage.class);
    when(response.getSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("修改成功");
    when(response.getData()).thenReturn(data);

    when(assessmentScaleManageGatewayService.update(request)).thenReturn(response);

    Result<?> result = controller.update(request);

    assertEquals(200, result.getCode());
    assertEquals("修改成功", result.getMessage());
    assertSame(data, result.getData());

    verify(assessmentScaleExcelParserService).validateRuleList((List) rules);
    verify(assessmentScaleManageGatewayService).update(request);
  }

  @Test
  void update_whenValidateFails_shouldReturnError() {
    AssessmentScaleUpdateRequest request = mock(AssessmentScaleUpdateRequest.class);
    List<?> rules = Collections.emptyList();

    when(request.getRules()).thenReturn((List) rules);
    doThrow(new RuntimeException("规则校验失败"))
        .when(assessmentScaleExcelParserService).validateRuleList((List) rules);

    Result<?> result = controller.update(request);

    assertEquals(500, result.getCode());
    assertEquals("规则校验失败", result.getMessage());
    assertNull(result.getData());

    verify(assessmentScaleManageGatewayService, never()).update(any());
  }

  @Test
  void enable_success() {
    AssessmentScaleManageResponseMessage response = mock(AssessmentScaleManageResponseMessage.class);
    when(response.getSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("启用成功");
    when(response.getData()).thenReturn(null);

    when(assessmentScaleManageGatewayService.enable(1L)).thenReturn(response);

    Result<?> result = controller.enable(1L);

    assertEquals(200, result.getCode());
    assertEquals("启用成功", result.getMessage());
  }

  @Test
  void disable_success() {
    AssessmentScaleManageResponseMessage response = mock(AssessmentScaleManageResponseMessage.class);
    when(response.getSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("停用成功");
    when(response.getData()).thenReturn(null);

    when(assessmentScaleManageGatewayService.disable(1L)).thenReturn(response);

    Result<?> result = controller.disable(1L);

    assertEquals(200, result.getCode());
    assertEquals("停用成功", result.getMessage());
  }

  @Test
  void delete_success() {
    AssessmentScaleManageResponseMessage response = mock(AssessmentScaleManageResponseMessage.class);
    when(response.getSuccess()).thenReturn(true);
    when(response.getMessage()).thenReturn("删除成功");
    when(response.getData()).thenReturn(null);

    when(assessmentScaleManageGatewayService.delete(1L)).thenReturn(response);

    Result<?> result = controller.delete(1L);

    assertEquals(200, result.getCode());
    assertEquals("删除成功", result.getMessage());
  }
}