package com.cropdeal.payment_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.cropdeal.payment_service.controller.*.*(..))")
    public void controllerMethods() {}

    @Pointcut("execution(* com.cropdeal.payment_service.service.*.*(..))")
    public void serviceMethods() {}

    @Before("controllerMethods()")
    public void logBeforeController(JoinPoint joinPoint) {
        log.info("[CONTROLLER] Entering: {} | Args: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        log.info("[CONTROLLER] Exiting: {} | Response: {}", joinPoint.getSignature(), result);
    }

    @Before("serviceMethods()")
    public void logBeforeService(JoinPoint joinPoint) {
        log.info("[SERVICE] Entering: {} | Args: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterService(JoinPoint joinPoint, Object result) {
        log.info("[SERVICE] Exiting: {} | Result: {}", joinPoint.getSignature(), result);
    }
}
