package com.example.hena.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.example.hena.*.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("[BEFORE] " + joinPoint.getSignature().toShortString());
    }

    @AfterReturning(pointcut = "execution(* com.example.hena.*.service.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("[AFTER RETURNING] " + joinPoint.getSignature().toShortString() + " | Result: " + result);
    }

    @AfterThrowing(pointcut = "execution(* com.example.hena.*.service.*.*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        System.out.println("[EXCEPTION] " + joinPoint.getSignature().toShortString() + " | Error: " + ex.getMessage());
    }
}
