package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.dto.ResetPasswordRequest;
import com.sl.mentalhealth.service.PasswordGatewayService;
import com.sl.mentalhealth.vo.ResetPasswordResponseVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

  private final PasswordGatewayService passwordGatewayService;

  public PasswordController(PasswordGatewayService passwordGatewayService) {
    this.passwordGatewayService = passwordGatewayService;
  }

  @PostMapping("/reset")
  public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody ResetPasswordRequest request) {
    Map<String, Object> result = new HashMap<>();

    try {
      ResetPasswordResponseVO response = passwordGatewayService.resetPassword(request);
      result.put("success", true);
      result.put("message", response.getMessage());
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      result.put("success", false);
      result.put("message", e.getMessage() == null ? "密码重置失败" : e.getMessage());
      return ResponseEntity.badRequest().body(result);
    }
  }
}