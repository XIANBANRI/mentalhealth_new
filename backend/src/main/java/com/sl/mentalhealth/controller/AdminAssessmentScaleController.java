package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.config.UserContext;
import com.sl.mentalhealth.dto.AssessmentScaleUpdateRequest;
import com.sl.mentalhealth.kafka.message.AssessmentScaleManageRequestMessage;
import com.sl.mentalhealth.kafka.message.AssessmentScaleManageResponseMessage;
import com.sl.mentalhealth.service.AssessmentScaleExcelParserService;
import com.sl.mentalhealth.service.AssessmentScaleManageGatewayService;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/assessment-scale")
public class AdminAssessmentScaleController {

  private final AssessmentScaleManageGatewayService assessmentScaleManageGatewayService;
  private final AssessmentScaleExcelParserService assessmentScaleExcelParserService;

  public AdminAssessmentScaleController(
      AssessmentScaleManageGatewayService assessmentScaleManageGatewayService,
      AssessmentScaleExcelParserService assessmentScaleExcelParserService) {
    this.assessmentScaleManageGatewayService = assessmentScaleManageGatewayService;
    this.assessmentScaleExcelParserService = assessmentScaleExcelParserService;
  }

  @PostMapping("/import")
  public Result<?> importScale(@RequestParam("scaleCode") String scaleCode,
      @RequestParam("scaleName") String scaleName,
      @RequestParam("scaleType") String scaleType,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "operator", required = false) String operator,
      @RequestPart("questionFile") MultipartFile questionFile,
      @RequestPart("ruleFile") MultipartFile ruleFile) {
    try {
      String currentAdmin = UserContext.getUsername();
      String realOperator = (operator == null || operator.trim().isEmpty()) ? currentAdmin : operator;

      AssessmentScaleManageRequestMessage message = new AssessmentScaleManageRequestMessage();
      message.setRequestId(UUID.randomUUID().toString());
      message.setAction("IMPORT");
      message.setScaleCode(scaleCode);
      message.setScaleName(scaleName);
      message.setScaleType(scaleType);
      message.setDescription(description);
      message.setOperator(realOperator);
      message.setQuestionFileName(questionFile.getOriginalFilename());
      message.setRuleFileName(ruleFile.getOriginalFilename());
      message.setQuestions(assessmentScaleExcelParserService.parseQuestionExcel(questionFile));
      message.setRules(assessmentScaleExcelParserService.parseRuleExcel(ruleFile));

      AssessmentScaleManageResponseMessage response = assessmentScaleManageGatewayService.importScale(message);
      if (Boolean.TRUE.equals(response.getSuccess())) {
        return Result.success(response.getMessage(), response.getData());
      }
      return Result.error(response.getMessage());
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  @GetMapping("/list")
  public Result<?> list() {
    AssessmentScaleManageResponseMessage response = assessmentScaleManageGatewayService.listAll();
    if (Boolean.TRUE.equals(response.getSuccess())) {
      return Result.success(response.getMessage(), response.getData());
    }
    return Result.error(response.getMessage());
  }

  @GetMapping("/detail/{scaleId}")
  public Result<?> detail(@PathVariable Long scaleId) {
    AssessmentScaleManageResponseMessage response = assessmentScaleManageGatewayService.detail(scaleId);
    if (Boolean.TRUE.equals(response.getSuccess())) {
      return Result.success(response.getMessage(), response.getData());
    }
    return Result.error(response.getMessage());
  }

  @PostMapping("/update")
  public Result<?> update(@RequestBody AssessmentScaleUpdateRequest request) {
    try {
      assessmentScaleExcelParserService.validateRuleList(request.getRules());

      AssessmentScaleManageResponseMessage response = assessmentScaleManageGatewayService.update(request);
      if (Boolean.TRUE.equals(response.getSuccess())) {
        return Result.success(response.getMessage(), response.getData());
      }
      return Result.error(response.getMessage());
    } catch (Exception e) {
      return Result.error(e.getMessage());
    }
  }

  @PostMapping("/enable/{scaleId}")
  public Result<?> enable(@PathVariable Long scaleId) {
    AssessmentScaleManageResponseMessage response = assessmentScaleManageGatewayService.enable(scaleId);
    if (Boolean.TRUE.equals(response.getSuccess())) {
      return Result.success(response.getMessage(), response.getData());
    }
    return Result.error(response.getMessage());
  }

  @PostMapping("/disable/{scaleId}")
  public Result<?> disable(@PathVariable Long scaleId) {
    AssessmentScaleManageResponseMessage response = assessmentScaleManageGatewayService.disable(scaleId);
    if (Boolean.TRUE.equals(response.getSuccess())) {
      return Result.success(response.getMessage(), response.getData());
    }
    return Result.error(response.getMessage());
  }

  @PostMapping("/delete/{scaleId}")
  public Result<?> delete(@PathVariable Long scaleId) {
    AssessmentScaleManageResponseMessage response = assessmentScaleManageGatewayService.delete(scaleId);
    if (Boolean.TRUE.equals(response.getSuccess())) {
      return Result.success(response.getMessage(), response.getData());
    }
    return Result.error(response.getMessage());
  }
}