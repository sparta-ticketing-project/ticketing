package com.example.ticketing.global.util;

public class RedisKeyGenerator {

    private static final String SEAT_LOCK_PREFIX = "lock:seat:";
    private static final String USER_LOCK_PREFIX = "lock:user:";
    private static final String ORDER_LOCK_PREFIX = "lock:order:";

    public static final String SEAT_DETAIL_COUNT_PREFIX = "seatcount:seatdetail:";
    public static final String CONCERT_COUNT_PREFIX = "seatcount:concert:";

    private RedisKeyGenerator() {}

    public static String seatLock(Long seatId) {
        return SEAT_LOCK_PREFIX + seatId;
    }

    public static String userLock(Long userId) {
        return USER_LOCK_PREFIX + userId;
    }

    public static String orderLock(Long orderId) {
        return ORDER_LOCK_PREFIX + orderId;
    }

    public static String seatDetailSeatCount(Long seatDetailId) {
        return SEAT_DETAIL_COUNT_PREFIX + seatDetailId;
    }

    public static String concertSeatCount(Long concertId) {
        return CONCERT_COUNT_PREFIX + concertId;
    }

    public static Long extractIdFromKey(String key, String prefix) {
        if (key == null || prefix == null || !key.startsWith(prefix)) {
            throw new IllegalArgumentException("Invalid key or prefix: " + key);
        }
        try {
            return Long.parseLong(key.substring(prefix.length()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse ID from key: " + key);
        }
    }
}
