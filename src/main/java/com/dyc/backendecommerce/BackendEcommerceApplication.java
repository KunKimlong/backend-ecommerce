package com.dyc.backendecommerce;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@SpringBootApplication
public class BackendEcommerceApplication {
  @Value("${server.host}")
  private String host;

  @Value("${server.port}")
  private String port;

  public static void main(String[] args) {
    SpringApplication.run(BackendEcommerceApplication.class, args);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void logHostInfo() {
    log.info("Application is running on host: {}, port: {}", host, port);
    log.info("API Document: http://{}:{}/swagger-ui.html", host, port);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
        ;
      }
    };
  }
}
