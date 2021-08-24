package com.somecompany.warehouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Springboot class.
 */
@SpringBootApplication
public class WarehouseApplication {

  /**
   *
   * @param args
   */
  public static void main(String[] args) {
    SpringApplication.run(WarehouseApplication.class, args);
  }

  /**
   * @return ObjectMapper bean for the project.
   */
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

}
