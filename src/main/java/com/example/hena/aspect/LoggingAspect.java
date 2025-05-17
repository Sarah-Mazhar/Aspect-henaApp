package com.example.hena.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Log BEFORE any service method execution
    @Before("execution(* com.example.hena..service..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        String args = Arrays.stream(joinPoint.getArgs())
                .map(arg -> (arg != null && arg.toString().toLowerCase().contains("pass")) ? "***" : String.valueOf(arg))
                .collect(Collectors.joining(", "));
        logger.info("[BEFORE] {} | Args: [{}]", methodName, args);
    }

    // Log AFTER successful method return
    @AfterReturning(pointcut = "execution(* com.example.hena..service..*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("[AFTER RETURNING] {} | Result: {}", joinPoint.getSignature().toShortString(), result);
    }

    // Log if any exception occurs
    @AfterThrowing(pointcut = "execution(* com.example.hena..service..*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        logger.error("[EXCEPTION] {} | Error: {}", joinPoint.getSignature().toShortString(), ex.getMessage());
    }
}
