package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.config.UserContext;
import com.sl.mentalhealth.dto.AssessmentSubmitRequest;
import com.sl.mentalhealth.service.AssessmentGatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/student/assessment")
public class StudentAssessmentController {

  private final AssessmentGatewayService assessmentGatewayService;

  public StudentAssessmentController(AssessmentGatewayService assessmentGatewayService) {
    this.assessmentGatewayService = assessmentGatewayService;
  }

  @GetMapping("/scales")
  public ResponseEntity<Map<String, Object>> listScales() {
    Map<String, Object> result = new HashMap<>();
    try {
      result.put("success", true);
      result.put("message", "查询成功");
      result.put("data", assessmentGatewayService.listScales());
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      result.put("success", false);
      result.put("message", e.getMessage());
      return ResponseEntity.badRequest().body(result);
    }
  }

  @GetMapping("/detail/{scaleId}")
  public ResponseEntity<Map<String, Object>> getDetail(@PathVariable Long scaleId) {
    Map<String, Object> result = new HashMap<>();
    try {
      result.put("success", true);
      result.put("message", "查询成功");
      result.put("data", assessmentGatewayService.getDetail(scaleId));
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      result.put("success", false);
      result.put("message", e.getMessage());
      return ResponseEntity.badRequest().body(result);
    }
  }

  @PostMapping("/submit")
  public ResponseEntity<Map<String, Object>> submit(@RequestBody AssessmentSubmitRequest request) {
    Map<String, Object> result = new HashMap<>();
    try {
      request.setStudentId(UserContext.getUsername());
      result.put("success", true);
      result.put("message", "提交成功");
      result.put("data", assessmentGatewayService.submit(request));
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      result.put("success", false);
      result.put("message", e.getMessage());
      return ResponseEntity.badRequest().body(result);
    }
  }

  @GetMapping("/records")
  public ResponseEntity<Map<String, Object>> getRecords() {
    Map<String, Object> result = new HashMap<>();
    try {
      String studentId = UserContext.getUsername();
      result.put("success", true);
      result.put("message", "查询成功");
      result.put("data", assessmentGatewayService.getRecords(studentId));
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      result.put("success", false);
      result.put("message", e.getMessage());
      return ResponseEntity.badRequest().body(result);
    }
  }
}