package dev.siraj.restauron.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for rate limiting using Resilience4j.
 * Protects authentication endpoints from brute force and DOS attacks.
 */
@Configuration
public class RateLimitConfig {

    /**
     * Rate limiter for login attempts.
     * Allows 5 login attempts per 15 minutes per user.
     */
    @Bean
    public RateLimiter loginRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(5) // 5 attempts
                .limitRefreshPeriod(Duration.ofMinutes(15)) // per 15 minutes
                .timeoutDuration(Duration.ofSeconds(1)) // wait time for acquiring permission
                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        return registry.rateLimiter("login");
    }

    /**
     * Rate limiter for OTP generation.
     * Allows 3 OTP requests per 30 minutes per phone number.
     */
    @Bean
    public RateLimiter otpRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(3) // 3 requests
                .limitRefreshPeriod(Duration.ofMinutes(30)) // per 30 minutes
                .timeoutDuration(Duration.ofSeconds(1))
                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        return registry.rateLimiter("otp");
    }

    /**
     * Rate limiter for password reset requests.
     * Allows 3 reset requests per 1 hour per email.
     */
    @Bean
    public RateLimiter passwordResetRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(3) // 3 requests
                .limitRefreshPeriod(Duration.ofHours(1)) // per 1 hour
                .timeoutDuration(Duration.ofSeconds(1))
                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        return registry.rateLimiter("password-reset");
    }

    /**
     * General API rate limiter for other endpoints.
     * Allows 100 requests per minute.
     */
    @Bean
    public RateLimiter apiRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(100) // 100 requests
                .limitRefreshPeriod(Duration.ofMinutes(1)) // per minute
                .timeoutDuration(Duration.ofSeconds(1))
                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        return registry.rateLimiter("api");
    }
}
