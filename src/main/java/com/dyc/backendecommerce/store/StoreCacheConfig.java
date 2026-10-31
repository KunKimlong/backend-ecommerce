package com.dyc.backendecommerce.store;

import com.dyc.backendecommerce.store.store.StoreInfoResponse;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

/** Store info (name, description, logos, favicon, contact details) rarely changes, so it is cached in Redis until Store settings are saved. */
@Configuration
public class StoreCacheConfig {
  public static final String REDIS_CACHE_MANAGER = "redisCacheManager";
  public static final String STORE_INFO = "storeInfo";

  @Bean(REDIS_CACHE_MANAGER)
  public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration storeInfo =
        RedisCacheConfiguration.defaultCacheConfig()
            .prefixCacheNameWith("ecommerce:")
            // Saving Store settings clears it right away; the expiry only covers changes made outside the app
            .entryTtl(Duration.ofHours(24))
            .disableCachingNullValues()
            // Typed JSON: readable in redis-cli and never deserializes into an arbitrary class
            .serializeValuesWith(
                SerializationPair.fromSerializer(
                    new Jackson2JsonRedisSerializer<>(StoreInfoResponse.class)));

    return RedisCacheManager.builder(connectionFactory)
        .withCacheConfiguration(STORE_INFO, storeInfo)
        .disableCreateOnMissingCache()
        .build();
  }
}
