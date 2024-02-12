package ru.abradox.middlewareservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String COMPETITION_INFO_CACHE = "competitionInfoCache";
    public static final int COMPETITION_INFO_CACHE_DURATION = 1;

    public static final String HISTORY_INFO_CACHE = "historyInfoCache";
    public static final int HISTORY_INFO_CACHE_DURATION = 1;

    public static final String TOKEN_INFO_CACHE = "tokenInfoCache";
    public static final int TOKEN_INFO_CACHE_DURATION = 3;

    @Bean
    public Cache competitionInfoCache() {
        return new CaffeineCache(COMPETITION_INFO_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(COMPETITION_INFO_CACHE_DURATION, TimeUnit.SECONDS)
                .build());
    }

    @Bean
    public Cache historyInfoCache() {
        return new CaffeineCache(HISTORY_INFO_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(HISTORY_INFO_CACHE_DURATION, TimeUnit.SECONDS)
                .build());
    }

    @Bean
    public Cache tokenInfoCache() {
        return new CaffeineCache(TOKEN_INFO_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(TOKEN_INFO_CACHE_DURATION, TimeUnit.SECONDS)
                .build());
    }
}
