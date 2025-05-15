package com.example.hena.redis.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String keyPrefix();
    String keyIdentifierExpression();
    long leaseTime() default 30;
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
