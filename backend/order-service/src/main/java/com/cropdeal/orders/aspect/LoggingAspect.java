package com.cropdeal.orders.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Pointcut for all controller methods
    @Pointcut("execution(* com.cropdeal.orders.resources.*.*(..))")
    public void controllerMethods() {}

    // Pointcut for all service methods
    @Pointcut("execution(* com.cropdeal.orders.services.*.*(..))")
    public void serviceMethods() {}

    // Log before controller method execution
    @Before("controllerMethods()")
    public void logBeforeController(JoinPoint joinPoint) {
        log.info("[CONTROLLER] Entering: {} | Args: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    // Log after controller method execution
    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        log.info("[CONTROLLER] Exiting: {} | Response: {}", joinPoint.getSignature(), result);
    }

    // Log before service method execution
    @Before("serviceMethods()")
    public void logBeforeService(JoinPoint joinPoint) {
        log.info("[SERVICE] Entering: {} | Args: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    // Log after service method execution
    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterService(JoinPoint joinPoint, Object result) {
        log.info("[SERVICE] Exiting: {} | Result: {}", joinPoint.getSignature(), result);
    }
}
