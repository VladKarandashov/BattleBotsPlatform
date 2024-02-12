package ru.abradox.platformgateway.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String USER_INFO_CACHE = "getCrmUserInfoByIdCache";

    @Bean
    public CaffeineCacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES));
        cacheManager.setAsyncCacheMode(true);
        return cacheManager;
    }

    @Bean
    public Cache getCrmUserById() {
        return new CaffeineCache(USER_INFO_CACHE, Caffeine.newBuilder()
                .build());
    }
}
