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

@Aspect
@Component
@Order(1)
public class RateLimitAspect {

    @Autowired
    private Redis redis;

    @Autowired
    private HttpServletRequest request;

    @Around("@annotation(rateLimit)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String ip = request.getRemoteAddr();
        String key = "rate:" + rateLimit.keyPrefix() + ":" + ip;
        long duration = rateLimit.timeUnit().toSeconds(rateLimit.duration());

        Long current = redis.increment(key, Duration.ofSeconds(duration));

        if (current != null && current > rateLimit.limit()) {
            throw new RateLimitExceededException("Too many requests. Please wait a moment before retrying.");
        }

        return joinPoint.proceed();
    }
}
