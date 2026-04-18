package com.sl.mentalhealth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
@MapperScan("com.sl.mentalhealth.mapper")
public class MentalHealthApplication {

  public static void main(String[] args) {
    SpringApplication.run(MentalHealthApplication.class, args);
  }
}