package com.example.ticketing.global.lock.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class LockRedisRepository {

    private final StringRedisTemplate redisTemplate;

    private static final Long UNLOCK_SUCCESS_RESULT = 1L;

    public boolean lock(String key, String value, long timeoutMs) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, timeoutMs, TimeUnit.MILLISECONDS);
        return Boolean.TRUE.equals(result);
    }

    public boolean unlock(String key, String value) {
        String unlockIfLockOwnerScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "return redis.call('del', KEYS[1]) else return 0 end";

        DefaultRedisScript<Long> redisUnlockScript = new DefaultRedisScript<>(unlockIfLockOwnerScript, Long.class);
        Long result = redisTemplate.execute(redisUnlockScript, Collections.singletonList(key), value);
        return UNLOCK_SUCCESS_RESULT.equals(result);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
