package com.example.hena.redis.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    long limit();                // Max requests
    long duration();             // Window length
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    String keyPrefix() default "";
}
