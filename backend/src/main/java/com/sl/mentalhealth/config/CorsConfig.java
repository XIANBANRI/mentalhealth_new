package com.sl.mentalhealth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Value("${app.cors.allowed-origins:http://localhost,http://localhost:80,http://localhost:8081,http://localhost:8082,http://localhost:5173}")
  private String allowedOrigins;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    String[] origins = StringUtils.commaDelimitedListToStringArray(allowedOrigins);

    registry.addMapping("/**")
        .allowedOriginPatterns(origins)
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
  }
}