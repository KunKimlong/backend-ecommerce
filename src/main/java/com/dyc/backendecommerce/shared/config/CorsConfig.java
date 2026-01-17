package com.dyc.backendecommerce.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();

    // ✅ Allow Next.js frontend
    config.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:3001"
    ));

    // ✅ Allow credentials (cookies, JWT)
    config.setAllowCredentials(true);

    // ✅ Allow all headers
    config.addAllowedHeader("*");

    // ✅ Allow all methods
    config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
    ));

    // ✅ Expose headers to frontend
    config.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-XSRF-TOKEN"
    ));

    // ✅ Cache preflight for 1 hour
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }
}