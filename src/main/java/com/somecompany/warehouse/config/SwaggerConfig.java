package com.somecompany.warehouse.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger Config Class
 */
@Configuration
public class SwaggerConfig {

  /**
   * Swagger Bean
   *
   * @return OpenAPI bean with details
   */
  @Bean
  public OpenAPI wareHouseOpenAPI() {
    return new OpenAPI()
        .info(new Info().title("Warehouse API")
            .description("Spring web app of warehouse endpoints")
            .version("v0.0.1")
            .license(new License().name("Apache 2.0").url("http://springdoc.org")));
  }
}