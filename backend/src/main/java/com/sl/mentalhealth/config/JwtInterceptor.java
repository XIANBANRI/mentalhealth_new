package com.sl.mentalhealth.config;

import com.sl.mentalhealth.common.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String uri = request.getRequestURI();

    // 放行跨域预检请求
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      return true;
    }

    // 放行登录和找回密码
    if ("/api/auth/login".equals(uri) || "/api/password/reset".equals(uri)) {
      return true;
    }

    String authorization = request.getHeader("Authorization");
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "未登录或Token缺失");
      return false;
    }

    String token = authorization.substring(7);

    try {
      Claims claims = JwtUtil.parseToken(token);
      String username = claims.get("username") == null ? null : claims.get("username").toString();
      String role = claims.get("role") == null ? null : claims.get("role").toString();

      if (username == null || username.trim().isEmpty() || role == null || role.trim().isEmpty()) {
        writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "Token信息不完整");
        return false;
      }

      if (!hasPathPermission(uri, role)) {
        writeJson(response, HttpServletResponse.SC_FORBIDDEN, "无权限访问该接口");
        return false;
      }

      UserContext.set(new LoginUser(username, role));
      return true;
    } catch (Exception e) {
      writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "Token无效或已过期");
      return false;
    }
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    UserContext.clear();
  }

  private boolean hasPathPermission(String uri, String role) {
    if (uri.startsWith("/api/admin/")) {
      return "admin".equalsIgnoreCase(role);
    }
    if (uri.startsWith("/api/teacher/")) {
      return "teacher".equalsIgnoreCase(role);
    }
    if (uri.startsWith("/api/student/")) {
      return "student".equalsIgnoreCase(role);
    }
    if (uri.startsWith("/api/counselor/")) {
      return "counselor".equalsIgnoreCase(role);
    }
    return true;
  }

  private void writeJson(HttpServletResponse response, int status, String message) throws Exception {
    response.setStatus(status);
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;charset=UTF-8");

    String safeMessage = message == null ? "" : message.replace("\"", "\\\"");
    String json = "{\"success\":false,\"code\":" + status + ",\"message\":\"" + safeMessage + "\"}";

    response.getWriter().write(json);
  }
}