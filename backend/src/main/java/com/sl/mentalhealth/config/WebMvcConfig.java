package com.sl.mentalhealth.config;

import com.sl.mentalhealth.service.TokenRedisService;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Value("${app.upload.avatar-root-dir:E:/mental_health/backend/uploads/avatar}")
  private String avatarRootDir;

  private final TokenRedisService tokenRedisService;

  public WebMvcConfig(TokenRedisService tokenRedisService) {
    this.tokenRedisService = tokenRedisService;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String location = Paths.get(avatarRootDir).toAbsolutePath().normalize().toUri().toString();

    registry.addResourceHandler("/files/avatar/**")
        .addResourceLocations(location);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new JwtInterceptor(tokenRedisService))
        .addPathPatterns("/api/**")
        .excludePathPatterns(
            "/api/auth/login",
            "/api/password/reset"
        );
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("http://localhost:8081")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
  }
}