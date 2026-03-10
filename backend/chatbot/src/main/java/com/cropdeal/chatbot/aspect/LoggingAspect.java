package com.cropdeal.chatbot.aspect;

//import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Pointcut for all methods in controller package
    @Pointcut("within(com.cropdeal.chatbot.controller..*)")
    public void controllerMethods() {}

    // Pointcut for all methods in service package
    @Pointcut("within(com.cropdeal.chatbot.service..*)")
    public void serviceMethods() {}

    @Around("controllerMethods() || serviceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        logger.debug("Entering method: {} with arguments: {}", methodName, args);

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        logger.debug("Exiting method: {} with result: {}", methodName, result);
        logger.debug("Execution time: {} ms", duration);

        return result;
    }
}
