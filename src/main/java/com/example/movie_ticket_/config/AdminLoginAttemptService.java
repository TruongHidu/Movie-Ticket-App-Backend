package com.example.movie_ticket_.config;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Chống dò mật khẩu cho trang đăng nhập admin:
 * sai quá {@link #MAX_ATTEMPTS} lần (theo IP + email) thì tạm khóa {@link #LOCK_DURATION}.
 */
@Component
public class AdminLoginAttemptService {

    public static final int MAX_ATTEMPTS = 5;
    public static final Duration LOCK_DURATION = Duration.ofMinutes(10);

    private record Attempt(int failures, Instant lockedUntil, Instant lastFailure) {}

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    private static String key(String ip, String email) {
        return ip + "|" + (email == null ? "" : email.trim().toLowerCase());
    }

    /** Số phút còn bị khóa, 0 nếu không bị khóa. */
    public long minutesLocked(String ip, String email) {
        Attempt a = attempts.get(key(ip, email));
        if (a == null || a.lockedUntil() == null) return 0;
        Duration left = Duration.between(Instant.now(), a.lockedUntil());
        if (left.isNegative() || left.isZero()) {
            attempts.remove(key(ip, email));
            return 0;
        }
        return Math.max(1, (left.toSeconds() + 59) / 60);
    }

    /** Ghi nhận một lần sai, trả về số lần còn lại trước khi bị khóa. */
    public int recordFailure(String ip, String email) {
        Instant now = Instant.now();
        Attempt updated = attempts.compute(key(ip, email), (k, a) -> {
            int failures = 1;
            if (a != null && a.lastFailure().isAfter(now.minus(LOCK_DURATION))) {
                failures = a.failures() + 1;
            }
            Instant lockedUntil = failures >= MAX_ATTEMPTS ? now.plus(LOCK_DURATION) : null;
            return new Attempt(failures, lockedUntil, now);
        });
        cleanup(now);
        return Math.max(0, MAX_ATTEMPTS - updated.failures());
    }

    public void recordSuccess(String ip, String email) {
        attempts.remove(key(ip, email));
    }

    private void cleanup(Instant now) {
        if (attempts.size() < 1000) return;
        attempts.entrySet().removeIf(e -> e.getValue().lastFailure().isBefore(now.minus(LOCK_DURATION.multipliedBy(2))));
    }
}
