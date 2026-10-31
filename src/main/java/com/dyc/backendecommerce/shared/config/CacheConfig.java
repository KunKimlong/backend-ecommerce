package com.dyc.backendecommerce.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.LoggingCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Storefront reads that every home-page visit repeats. These live in memory; store info is the exception and
 * lives in Redis (see store/StoreCacheConfig).
 */
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {
  public static final String STORE_CATEGORIES = "storeCategories";
  public static final String STORE_BANNERS = "storeBanners";
  public static final String BEST_SELLERS = "bestSellers";

  @Bean
  @Primary
  public CacheManager cacheManager(@Value("${spring.cache.caffeine.spec}") String spec) {
    CaffeineCacheManager cacheManager =
        new CaffeineCacheManager(STORE_CATEGORIES, STORE_BANNERS, BEST_SELLERS);
    cacheManager.setCacheSpecification(spec);
    return cacheManager;
  }

  // A cache that can't be reached (e.g. Redis down) is logged and treated as a miss, so the data comes from the database
  @Override
  public CacheErrorHandler errorHandler() {
    return new LoggingCacheErrorHandler();
  }
}
