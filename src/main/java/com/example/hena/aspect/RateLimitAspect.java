package com.example.hena.aspect;

import com.example.hena.redis.annotations.RateLimit;
import com.example.hena.redis.exceptions.RateLimitExceededException;
import com.example.hena.redis.service.Redis;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Aspect for enforcing method-level rate limiting using Redis.
 * Intercepts methods annotated with @RateLimit and restricts access
 * based on the number of allowed calls from the client's IP address.
 */
@Aspect
@Component
@Order(1) // Ensures this aspect has high precedence if multiple aspects are applied
public class RateLimitAspect {

    @Autowired
    private Redis redis;

    @Autowired
    private HttpServletRequest request;

    /**
     * Around advice that intercepts method execution when annotated with @RateLimit.
     * Enforces rate limiting based on client IP address and annotation configuration.
     *
     * @param joinPoint the intercepted method call
     * @param rateLimit the annotation parameters for rate limiting
     * @return the result of method execution if within allowed limit
     * @throws Throwable if the method execution fails or rate limit is exceeded
     */
    @Around("@annotation(rateLimit)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // Get client IP address from the request
        String ip = request.getRemoteAddr();

        // Build Redis key using prefix + IP
        String key = "rate:" + rateLimit.keyPrefix() + ":" + ip;

        // Convert the duration to seconds for Redis TTL
        long duration = rateLimit.timeUnit().toSeconds(rateLimit.duration());

        // Increment the count in Redis and set TTL if it's a new key
        Long current = redis.increment(key, Duration.ofSeconds(duration));

        // If the limit is exceeded, throw an exception
        if (current != null && current > rateLimit.limit()) {
            throw new RateLimitExceededException("Too many requests. Please wait a moment before retrying.");
        }

        // Proceed with method execution
        return joinPoint.proceed();
    }
}
