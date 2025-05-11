/*package com.example.hena.aspect;

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
*/
package com.example.hena.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingAspect {

    // Log BEFORE method execution with arguments
    @Before("execution(* com.example.hena.*.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        String args = Arrays.stream(joinPoint.getArgs())
                .map(arg -> {
                    // Mask passwords
                    if (arg instanceof String && ((String) arg).toLowerCase().contains("pass")) {
                        return "***";
                    }
                    return arg != null ? arg.toString() : "null";
                })
                .collect(Collectors.joining(", "));
        System.out.println("[BEFORE] " + methodName + " | Args: [" + args + "]");
    }

    // Log AFTER successful return
    @AfterReturning(pointcut = "execution(* com.example.hena.*.service.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("[AFTER RETURNING] " + joinPoint.getSignature().toShortString() + " | Result: " + result);
    }

    // Log EXCEPTION
    @AfterThrowing(pointcut = "execution(* com.example.hena.*.service.*.*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        System.out.println("[EXCEPTION] " + joinPoint.getSignature().toShortString() + " | Error: " + ex.getMessage());
    }
}
