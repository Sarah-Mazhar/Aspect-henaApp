package com.example.hena.redis.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Annotation to specify rate limiting on a method.
 * Can be used in conjunction with AOP to prevent excessive invocation within a time window.
 */
@Target(ElementType.METHOD)              // Applicable only to methods
@Retention(RetentionPolicy.RUNTIME)     // Available at runtime for reflection/AOP
public @interface RateLimit {

    /**
     * Maximum number of allowed method invocations within the specified time window.
     */
    long limit();

    /**
     * Duration of the time window for rate limiting.
     */
    long duration();

    /**
     * Time unit for the duration (e.g., SECONDS, MINUTES).
     * Defaults to seconds.
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * Optional key prefix used for generating a unique Redis key for rate tracking.
     */
    String keyPrefix() default "";
}
