package com.example.hena.redis.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Annotation for applying a distributed lock using Redis.
 * Intended to prevent concurrent execution of the same logic
 * across multiple instances or threads, based on a dynamic key.
 */
@Target(ElementType.METHOD)              // Can only be applied to methods
@Retention(RetentionPolicy.RUNTIME)     // Retained at runtime for AOP processing
public @interface DistributedLock {

    /**
     * Prefix used for generating the Redis lock key.
     * Helps namespace the lock to avoid collisions across different lock types.
     */
    String keyPrefix();

    /**
     * SpEL (Spring Expression Language) expression to dynamically compute the key identifier.
     * This is typically based on method arguments.
     */
    String keyIdentifierExpression();

    /**
     * The amount of time the lock should be held before it expires automatically.
     * Default is 30 units (based on timeUnit).
     */
    long leaseTime() default 30;

    /**
     * The time unit used for the lease time.
     * Default is SECONDS.
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
