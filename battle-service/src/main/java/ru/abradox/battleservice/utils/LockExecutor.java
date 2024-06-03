package ru.abradox.battleservice.utils;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class LockExecutor {

    private final RedissonClient client;

    public void execute(String key, Integer leaseTimeInSeconds, Runnable method) {
        RLock lock = client.getFairLock(key);
        lock.lock(leaseTimeInSeconds, TimeUnit.SECONDS);
        try {
            method.run();
        } finally {
            lock.unlock();
        }
    }
}
