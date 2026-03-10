package com.cropdeal.dealer.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DealerLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(DealerLoggingAspect.class);

    // Controller Logging for DealerController

    @Before("execution(* com.cropdeal.dealer.controller.DealerController.getProfile(..))")
    public void beforeGetProfile(JoinPoint jp) {
        logger.info("[GET PROFILE] Entering getProfile with args: {}", jp.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.dealer.controller.DealerController.getProfile(..))", returning = "result")
    public void afterGetProfile(Object result) {
        logger.info("[GET PROFILE] Exiting getProfile with response: {}", result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.dealer.controller.DealerController.getProfile(..))", throwing = "ex")
    public void onGetProfileException(Exception ex) {
        logger.error("[GET PROFILE] Exception: {}", ex.getMessage(), ex);
    }

    @Before("execution(* com.cropdeal.dealer.controller.DealerController.updateProfile(..))")
    public void beforeUpdateProfile(JoinPoint jp) {
        logger.info("[UPDATE PROFILE] Entering updateProfile with args: {}", jp.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.dealer.controller.DealerController.updateProfile(..))", returning = "result")
    public void afterUpdateProfile(Object result) {
        logger.info("[UPDATE PROFILE] Exiting updateProfile with response: {}", result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.dealer.controller.DealerController.updateProfile(..))", throwing = "ex")
    public void onUpdateProfileException(Exception ex) {
        logger.error("[UPDATE PROFILE] Exception: {}", ex.getMessage(), ex);
    }

    @Before("execution(* com.cropdeal.dealer.controller.DealerController.viewAllCrops(..))")
    public void beforeViewAllCrops(JoinPoint jp) {
        logger.info("[VIEW CROPS] Entering viewAllCrops");
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.dealer.controller.DealerController.viewAllCrops(..))", returning = "result")
    public void afterViewAllCrops(Object result) {
        logger.info("[VIEW CROPS] Exiting viewAllCrops with response: {}", result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.dealer.controller.DealerController.viewAllCrops(..))", throwing = "ex")
    public void onViewAllCropsException(Exception ex) {
        logger.error("[VIEW CROPS] Exception: {}", ex.getMessage(), ex);
    }

    @Before("execution(* com.cropdeal.dealer.controller.DealerController.getAllDealers(..))")
    public void beforeGetAllDealers(JoinPoint jp) {
        logger.info("[GET ALL DEALERS] Entering getAllDealers");
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.dealer.controller.DealerController.getAllDealers(..))", returning = "result")
    public void afterGetAllDealers(Object result) {
        logger.info("[GET ALL DEALERS] Exiting getAllDealers with response size: {}", (result instanceof java.util.List ? ((java.util.List<?>) result).size() : "N/A"));
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.dealer.controller.DealerController.getAllDealers(..))", throwing = "ex")
    public void onGetAllDealersException(Exception ex) {
        logger.error("[GET ALL DEALERS] Exception: {}", ex.getMessage(), ex);
    }
}
