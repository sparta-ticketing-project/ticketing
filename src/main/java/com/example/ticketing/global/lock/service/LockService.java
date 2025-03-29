package com.example.ticketing.global.lock.service;

import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import com.example.ticketing.global.lock.repository.LockRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LockService {

    private final LockRedisRepository lockRedisRepository;

    private static final long LOCK_TIMEOUT_MS = 3000; // 3초
    private static final long MAX_RETRY_TIMES = 3;
    private static final long RETRY_WAIT_MS = 100;

    public Map<String, String> acquireLocks(List<String> keys) {
        List<String> sortedKeys = new ArrayList<>(keys);
        Collections.sort(sortedKeys);

        Map<String, String> acquired = new HashMap<>();

        try {
            for (String key : sortedKeys) {
                String value = createUniqueLockValue();
                boolean success = tryLockWithRetry(key, value);

                if (!success) {
                    releaseLocks(acquired);
                    throw new CustomException(ExceptionType.RESOURCE_LOCKED);
                }
                acquired.put(key, value);
            }

            return acquired;
        } catch (Exception e) {
            releaseLocks(acquired);
            throw e;
        }
    }

    public Map<String, String> acquireLock(String key) {
        String value = createUniqueLockValue();
        boolean success = tryLockWithRetry(key, value);

        if (!success) {
            throw new CustomException(ExceptionType.RESOURCE_LOCKED);
        }

        Map<String, String> acquired = new HashMap<>();
        acquired.put(key, value);
        return acquired;
    }

    public void releaseLocks(Map<String, String> locks) {
        for (Map.Entry<String, String> entry : locks.entrySet()) {
            boolean success = lockRedisRepository.unlock(entry.getKey(), entry.getValue());

            if (!success) { // TTL 만료
                throw new CustomException(ExceptionType.LOCK_OWNERSHIP_LOST);
            }
        }
    }

    private boolean tryLockWithRetry(String key, String value) {
        for (int i = 0; i < MAX_RETRY_TIMES; i++) {
            boolean locked = lockRedisRepository.lock(key, value, LOCK_TIMEOUT_MS);
            if (locked) return true;

            try {
                Thread.sleep(RETRY_WAIT_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return false;
    }

    private String createUniqueLockValue() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
