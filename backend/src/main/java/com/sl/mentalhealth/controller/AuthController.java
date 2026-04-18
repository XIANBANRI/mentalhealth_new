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
    System.out.println("收到登录请求: role=" + request.getRole() + ", username=" + request.getUsername());
    try {
      LoginResponseVO responseVO = authGatewayService.login(request);
      System.out.println("登录成功返回");
      return Result.success("登录成功", responseVO);
    } catch (IllegalArgumentException e) {
      System.out.println("参数异常: " + e.getMessage());
      return Result.badRequest(e.getMessage());
    } catch (Exception e) {
      System.out.println("登录异常: " + e.getMessage());
      return Result.error(e.getMessage());
    }
  }
}