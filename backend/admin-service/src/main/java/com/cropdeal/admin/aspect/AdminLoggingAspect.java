package com.cropdeal.admin.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AdminLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(AdminLoggingAspect.class);

    // Controller Layer Logging

    @Before("execution(* com.cropdeal.admin.controller.AdminController.*(..))")
    public void beforeControllerMethod(JoinPoint jp) {
        logger.info("[ADMIN CONTROLLER] Entering: {} with args: {}", jp.getSignature(), jp.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.admin.controller.AdminController.*(..))", returning = "result")
    public void afterControllerMethod(JoinPoint jp, Object result) {
        logger.info("[ADMIN CONTROLLER] Exiting: {} with response: {}", jp.getSignature(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.admin.controller.AdminController.*(..))", throwing = "ex")
    public void onControllerException(JoinPoint jp, Exception ex) {
        logger.error("[ADMIN CONTROLLER] Exception in {}: {}", jp.getSignature(), ex.getMessage(), ex);
    }

    // Service Layer Logging

    @Before("execution(* com.cropdeal.admin.service.*.*(..))")
    public void beforeServiceMethod(JoinPoint jp) {
        logger.info("[ADMIN SERVICE] Entering: {} with args: {}", jp.getSignature(), jp.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.admin.service.*.*(..))", returning = "result")
    public void afterServiceMethod(JoinPoint jp, Object result) {
        logger.info("[ADMIN SERVICE] Exiting: {} with response: {}", jp.getSignature(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.admin.service.*.*(..))", throwing = "ex")
    public void onServiceException(JoinPoint jp, Exception ex) {
        logger.error("[ADMIN SERVICE] Exception in {}: {}", jp.getSignature(), ex.getMessage(), ex);
    }
}
