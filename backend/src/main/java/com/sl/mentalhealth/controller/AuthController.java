package com.sl.mentalhealth.controller;

import com.sl.mentalhealth.common.Result;
import com.sl.mentalhealth.dto.LoginRequest;
import com.sl.mentalhealth.service.AuthGatewayService;
import com.sl.mentalhealth.vo.LoginResponseVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthGatewayService authGatewayService;

  public AuthController(AuthGatewayService authGatewayService) {
    this.authGatewayService = authGatewayService;
  }

  @PostMapping("/login")
  public Result<LoginResponseVO> login(@RequestBody LoginRequest request) {
    try {
      LoginResponseVO responseVO = authGatewayService.login(request);
      return Result.success("登录成功", responseVO);
    } catch (IllegalArgumentException e) {
      return Result.badRequest(e.getMessage());
    } catch (Exception e) {
      return Result.error(e.getMessage() == null ? "登录失败" : e.getMessage());
    }
  }
}