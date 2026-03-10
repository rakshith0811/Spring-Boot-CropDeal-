package com.cropdeal.farmer.aspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class FarmerControllerLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(FarmerControllerLoggingAspect.class);

    // Profile Update
    @Before("execution(* com.cropdeal.farmer.controller.FarmerController.updateProfile(..))")
    public void beforeUpdateProfile(JoinPoint jp) {
        logger.info("[FARMER][UPDATE_PROFILE] Entering updateProfile with args: {}", Arrays.toString(jp.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.updateProfile(..))", returning = "result")
    public void afterUpdateProfile(Object result) {
        logger.info("[FARMER][UPDATE_PROFILE] Exiting updateProfile with response: {}", result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.updateProfile(..))", throwing = "ex")
    public void onUpdateProfileException(Exception ex) {
        logger.error("[FARMER][UPDATE_PROFILE] Exception: {}", ex.getMessage(), ex);
    }

    // Get Farmer Profile
    @Before("execution(* com.cropdeal.farmer.controller.FarmerController.getProfile(..))")
    public void beforeGetProfile(JoinPoint jp) {
        logger.info("[FARMER][GET_PROFILE] Entering getProfile with args: {}", Arrays.toString(jp.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.getProfile(..))", returning = "result")
    public void afterGetProfile(Object result) {
        logger.info("[FARMER][GET_PROFILE] Exiting getProfile with response: {}", result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.getProfile(..))", throwing = "ex")
    public void onGetProfileException(Exception ex) {
        logger.error("[FARMER][GET_PROFILE] Exception: {}", ex.getMessage(), ex);
    }

    // Add Crop
    @Before("execution(* com.cropdeal.farmer.controller.FarmerController.addCrop(..))")
    public void beforeAddCrop(JoinPoint jp) {
        logger.info("[FARMER][ADD_CROP] Entering addCrop with args: {}", Arrays.toString(jp.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.addCrop(..))", returning = "result")
    public void afterAddCrop(Object result) {
        logger.info("[FARMER][ADD_CROP] Exiting addCrop with response: {}", result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.addCrop(..))", throwing = "ex")
    public void onAddCropException(Exception ex) {
        logger.error("[FARMER][ADD_CROP] Exception: {}", ex.getMessage(), ex);
    }

    // Get Farmer Crops
    @Before("execution(* com.cropdeal.farmer.controller.FarmerController.getFarmerCrops(..))")
    public void beforeGetFarmerCrops(JoinPoint jp) {
        logger.info("[FARMER][GET_CROPS] Entering getFarmerCrops with args: {}", Arrays.toString(jp.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.getFarmerCrops(..))", returning = "result")
    public void afterGetFarmerCrops(Object result) {
        logger.info("[FARMER][GET_CROPS] Exiting getFarmerCrops with response size: {}", 
                    (result instanceof List<?> list) ? list.size() : result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.getFarmerCrops(..))", throwing = "ex")
    public void onGetFarmerCropsException(Exception ex) {
        logger.error("[FARMER][GET_CROPS] Exception: {}", ex.getMessage(), ex);
    }

    // Update Crop
    @Before("execution(* com.cropdeal.farmer.controller.FarmerController.updateCrop(..))")
    public void beforeUpdateCrop(JoinPoint jp) {
        logger.info("[FARMER][UPDATE_CROP] Entering updateCrop with args: {}", Arrays.toString(jp.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.updateCrop(..))", returning = "result")
    public void afterUpdateCrop(Object result) {
        logger.info("[FARMER][UPDATE_CROP] Exiting updateCrop with response: {}", result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.updateCrop(..))", throwing = "ex")
    public void onUpdateCropException(Exception ex) {
        logger.error("[FARMER][UPDATE_CROP] Exception: {}", ex.getMessage(), ex);
    }

    // Delete Crop
    @Before("execution(* com.cropdeal.farmer.controller.FarmerController.deleteCrop(..))")
    public void beforeDeleteCrop(JoinPoint jp) {
        logger.info("[FARMER][DELETE_CROP] Entering deleteCrop with args: {}", Arrays.toString(jp.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.deleteCrop(..))", returning = "result")
    public void afterDeleteCrop(Object result) {
        logger.info("[FARMER][DELETE_CROP] Exiting deleteCrop with response: {}", result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.deleteCrop(..))", throwing = "ex")
    public void onDeleteCropException(Exception ex) {
        logger.error("[FARMER][DELETE_CROP] Exception: {}", ex.getMessage(), ex);
    }

    // Get Public Crops
    @Before("execution(* com.cropdeal.farmer.controller.FarmerController.getPublicCrops(..))")
    public void beforeGetPublicCrops() {
        logger.info("[FARMER][GET_PUBLIC_CROPS] Entering getPublicCrops");
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.getPublicCrops(..))", returning = "result")
    public void afterGetPublicCrops(Object result) {
        logger.info("[FARMER][GET_PUBLIC_CROPS] Exiting getPublicCrops with response size: {}", 
                    (result instanceof List<?> list) ? list.size() : result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.getPublicCrops(..))", throwing = "ex")
    public void onGetPublicCropsException(Exception ex) {
        logger.error("[FARMER][GET_PUBLIC_CROPS] Exception: {}", ex.getMessage(), ex);
    }

    // Get All Farmers (DTO)
    @Before("execution(* com.cropdeal.farmer.controller.FarmerController.getAllFarmers(..))")
    public void beforeGetAllFarmers() {
        logger.info("[FARMER][GET_ALL] Entering getAllFarmers");
    }

    @AfterReturning(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.getAllFarmers(..))", returning = "result")
    public void afterGetAllFarmers(Object result) {
        logger.info("[FARMER][GET_ALL] Exiting getAllFarmers with response size: {}", 
                    (result instanceof List<?> list) ? list.size() : result);
    }

    @AfterThrowing(pointcut = "execution(* com.cropdeal.farmer.controller.FarmerController.getAllFarmers(..))", throwing = "ex")
    public void onGetAllFarmersException(Exception ex) {
        logger.error("[FARMER][GET_ALL] Exception: {}", ex.getMessage(), ex);
    }
}







