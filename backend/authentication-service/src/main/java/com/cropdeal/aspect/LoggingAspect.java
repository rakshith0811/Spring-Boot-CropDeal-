package com.cropdeal.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Explicit logging for AuthController methods

    @Before("execution(* com.cropdeal.controller.AuthController.createAuthenticationToken(..))")
    public void beforeSignin(JoinPoint jp) {
        logger.info("[SIGNIN] Entering signin with args: {}", jp.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.controller.AuthController.createAuthenticationToken(..))", returning = "result")
    public void afterSignin(Object result) {
        logger.info("[SIGNIN] Exiting signin with response: {}", result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.controller.AuthController.createAuthenticationToken(..))", throwing = "ex")
    public void onSigninException(Exception ex) {
        logger.error("[SIGNIN] Exception: {}", ex.getMessage(), ex);
    }

    @Before("execution(* com.cropdeal.controller.AuthController.registerUser(..))")
    public void beforeSignup(JoinPoint jp) {
        logger.info("[SIGNUP] Entering signup with args: {}", jp.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.controller.AuthController.registerUser(..))", returning = "result")
    public void afterSignup(Object result) {
        logger.info("[SIGNUP] Exiting signup with response: {}", result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.controller.AuthController.registerUser(..))", throwing = "ex")
    public void onSignupException(Exception ex) {
        logger.error("[SIGNUP] Exception: {}", ex.getMessage(), ex);
    }

    @Before("execution(* com.cropdeal.controller.AuthController.validateToken(..))")
    public void beforeValidate(JoinPoint jp) {
        logger.info("[VALIDATE] Entering validateToken with args: {}", jp.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.controller.AuthController.validateToken(..))", returning = "result")
    public void afterValidate(Object result) {
        logger.info("[VALIDATE] Exiting validateToken with response: {}", result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.controller.AuthController.validateToken(..))", throwing = "ex")
    public void onValidateException(Exception ex) {
        logger.error("[VALIDATE] Exception: {}", ex.getMessage(), ex);
    }

    @Before("execution(* com.cropdeal.controller.AuthController.healthCheck(..))")
    public void beforeHealth(JoinPoint jp) {
        logger.info("[HEALTH] Entering healthCheck");
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.controller.AuthController.healthCheck(..))", returning = "result")
    public void afterHealth(Object result) {
        logger.info("[HEALTH] Exiting healthCheck with response: {}", result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.controller.AuthController.healthCheck(..))", throwing = "ex")
    public void onHealthException(Exception ex) {
        logger.error("[HEALTH] Exception: {}", ex.getMessage(), ex);
    }

    // Service Layer Logging

    @Before("execution(* com.cropdeal.service.*.*(..))")
    public void beforeServiceMethods(JoinPoint jp) {
        logger.info("Entering Service Method: {} | Args: {}", jp.getSignature(), jp.getArgs());
    }

    @AfterReturning("execution(* com.cropdeal.service.*.*(..))")
    public void afterServiceMethods(JoinPoint jp) {
        logger.info("Exiting Service Method: {}", jp.getSignature());
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.service.*.*(..))", throwing = "ex")
    public void onServiceException(JoinPoint jp, Exception ex) {
        logger.error("Exception in Service Method: {} | Message: {}", jp.getSignature(), ex.getMessage(), ex);
    }

    // JWT Util Logging

    @Before("execution(* com.cropdeal.util.JwtUtil.generateToken(..))")
    public void beforeGenerateToken(JoinPoint jp) {
        logger.info("Generating JWT Token for user: {}", jp.getArgs()[0]);
    }

    @AfterReturning("execution(* com.cropdeal.util.JwtUtil.generateToken(..))")
    public void afterGenerateToken() {
        logger.info("JWT Token generated successfully");
    }

    @Before("execution(* com.cropdeal.util.JwtUtil.validateToken(..))")
    public void beforeValidateToken() {
        logger.info("Validating JWT Token");
    }

    @AfterReturning("execution(* com.cropdeal.util.JwtUtil.validateToken(..))")
    public void afterValidateToken() {
        logger.info("JWT Token validated successfully");
    }

    @Before("execution(* com.cropdeal.util.JwtUtil.extractUsername(..))")
    public void beforeExtractUsername() {
        logger.info("Extracting username from token");
    }

    @AfterReturning("execution(* com.cropdeal.util.JwtUtil.extractUsername(..))")
    public void afterExtractUsername() {
        logger.info("Username extracted from token");
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.util.JwtUtil.*(..))", throwing = "ex")
    public void onJwtUtilException(JoinPoint jp, Exception ex) {
        logger.error("Exception in JwtUtil Method: {} | Message: {}", jp.getSignature(), ex.getMessage(), ex);
    }
}
