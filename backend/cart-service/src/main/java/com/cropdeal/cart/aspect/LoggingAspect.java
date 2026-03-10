package com.cropdeal.cart.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Controller layer logging
    @Before("execution(* com.cropdeal.cart.controller.CartController.*(..))")
    public void beforeControllerMethods(JoinPoint jp) {
        logger.info("[CONTROLLER] Entering {} with args: {}", jp.getSignature(), jp.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.cart.controller.CartController.*(..))", returning = "result")
    public void afterControllerMethods(JoinPoint jp, Object result) {
        logger.info("[CONTROLLER] Exiting {} with response: {}", jp.getSignature(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.cart.controller.CartController.*(..))", throwing = "ex")
    public void onControllerException(JoinPoint jp, Exception ex) {
        logger.error("[CONTROLLER] Exception in {}: {}", jp.getSignature(), ex.getMessage(), ex);
    }

    // Service layer logging
    @Before("execution(* com.cropdeal.cart.service.*.*(..))")
    public void beforeServiceMethods(JoinPoint jp) {
        logger.info("[SERVICE] Entering {} with args: {}", jp.getSignature(), jp.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.cart.service.*.*(..))")
    public void afterServiceMethods(JoinPoint jp) {
        logger.info("[SERVICE] Exiting {}", jp.getSignature());
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.cart.service.*.*(..))", throwing = "ex")
    public void onServiceException(JoinPoint jp, Exception ex) {
        logger.error("[SERVICE] Exception in {}: {}", jp.getSignature(), ex.getMessage(), ex);
    }
}
