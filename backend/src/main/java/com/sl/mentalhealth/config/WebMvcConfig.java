package com.sl.mentalhealth.config;

import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Value("${app.upload.avatar-root-dir:E:/mental_health/backend/uploads/avatar}")
  private String avatarRootDir;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String location = Paths.get(avatarRootDir).toAbsolutePath().normalize().toUri().toString();

    registry.addResourceHandler("/files/avatar/**")
        .addResourceLocations(location);
  }
}