package ru.abradox.battlegateway.service.impl;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class LimiterManager {
    
    private final ConcurrentMap<String, RateLimiter> keyRateLimiters = new ConcurrentHashMap<>();

    private final RateLimiterConfig config = RateLimiterConfig.custom()
            .timeoutDuration(Duration.ofMillis(100))
            .limitRefreshPeriod(Duration.ofSeconds(1))
            .limitForPeriod(8)
            .build();

    public RateLimiter getLimiter(String token) {
        return keyRateLimiters.compute(token, (key, limiter) ->
                (limiter == null) ? RateLimiter.of(token, config) : limiter);
    }
}