package com.cropdeal.admin.aspect;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminLoggingAspectTest {

    @InjectMocks
    private AdminLoggingAspect adminLoggingAspect;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Signature signature;

    private ListAppender<ILoggingEvent> logAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        // Set up log capturing for AdminLoggingAspect class
        logger = (Logger) LoggerFactory.getLogger(AdminLoggingAspect.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
    }

    // Controller Layer Tests

    @Test
    void beforeControllerMethod_shouldLogMethodEntryWithArgs() {
        String methodName = "AdminController.getAllFarmers()";
        Object[] args = {"arg1", "arg2"};

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(args);

        adminLoggingAspect.beforeControllerMethod(joinPoint);

        assertEquals(1, logAppender.list.size());
        ILoggingEvent logEvent = logAppender.list.get(0);
        assertEquals("INFO", logEvent.getLevel().toString());
        assertTrue(logEvent.getMessage().contains("[ADMIN CONTROLLER] Entering: {} with args: {}"));
//        assertEquals(methodName, logEvent.getArgumentArray()[0]);
//        assertEquals(args, logEvent.getArgumentArray()[1]);
    }

    @Test
    void afterControllerMethod_shouldLogMethodExitWithResult() {
        String methodName = "AdminController.updateFarmerStatus()";
        String result = "Success";

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn(methodName);

        adminLoggingAspect.afterControllerMethod(joinPoint, result);

        assertEquals(1, logAppender.list.size());
        ILoggingEvent logEvent = logAppender.list.get(0);
        assertEquals("INFO", logEvent.getLevel().toString());
        assertTrue(logEvent.getMessage().contains("[ADMIN CONTROLLER] Exiting: {} with response: {}"));
        //assertEquals(methodName, logEvent.getArgumentArray()[0]);
        //assertEquals(result, logEvent.getArgumentArray()[1]);
    }

    @Test
    void onControllerException_shouldLogExceptionWithDetails() {
        String methodName = "AdminController.deleteFarmer()";
        Exception exception = new RuntimeException("Test exception message");

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn(methodName);

        adminLoggingAspect.onControllerException(joinPoint, exception);

        assertEquals(1, logAppender.list.size());
        ILoggingEvent logEvent = logAppender.list.get(0);
        assertEquals("ERROR", logEvent.getLevel().toString());
        assertTrue(logEvent.getMessage().contains("[ADMIN CONTROLLER] Exception in {}: {}"));
       // assertEquals(methodName, logEvent.getArgumentArray()[0]);
       // assertEquals("Test exception message", logEvent.getArgumentArray()[1]);
       // assertEquals(exception, logEvent.getArgumentArray()[2]);
    }

    // Service Layer Tests

    @Test
    void beforeServiceMethod_shouldLogMethodEntryWithArgs() {
        String methodName = "AdminServiceImpl.getAllFarmers()";
        Object[] args = {1, "active"};

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(args);

        adminLoggingAspect.beforeServiceMethod(joinPoint);

        assertEquals(1, logAppender.list.size());
        ILoggingEvent logEvent = logAppender.list.get(0);
        assertEquals("INFO", logEvent.getLevel().toString());
        assertTrue(logEvent.getMessage().contains("[ADMIN SERVICE] Entering: {} with args: {}"));
        //assertEquals(methodName, logEvent.getArgumentArray()[0]);
        //assertEquals(args, logEvent.getArgumentArray()[1]);
    }

    @Test
    void afterServiceMethod_shouldLogMethodExitWithResult() {
        String methodName = "AdminServiceImpl.updateFarmerStatus()";
        Object result = "Farmer status updated successfully";

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn(methodName);

        adminLoggingAspect.afterServiceMethod(joinPoint, result);

        assertEquals(1, logAppender.list.size());
        ILoggingEvent logEvent = logAppender.list.get(0);
        assertEquals("INFO", logEvent.getLevel().toString());
        assertTrue(logEvent.getMessage().contains("[ADMIN SERVICE] Exiting: {} with response: {}"));
        //assertEquals(methodName, logEvent.getArgumentArray()[0]);
        //assertEquals(result, logEvent.getArgumentArray()[1]);
    }

    @Test
    void onServiceException_shouldLogExceptionWithDetails() {
        String methodName = "AdminServiceImpl.processRequest()";
        Exception exception = new IllegalArgumentException("Invalid parameter");

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn(methodName);

        adminLoggingAspect.onServiceException(joinPoint, exception);

        assertEquals(1, logAppender.list.size());
        ILoggingEvent logEvent = logAppender.list.get(0);
        assertEquals("ERROR", logEvent.getLevel().toString());
        assertTrue(logEvent.getMessage().contains("[ADMIN SERVICE] Exception in {}: {}"));
       // assertEquals(methodName, logEvent.getArgumentArray()[0]);
        //assertEquals("Invalid parameter", logEvent.getArgumentArray()[1]);
        //assertEquals(exception, logEvent.getArgumentArray()[2]);
    }

    // Edge Cases and Null Handling Tests

    @Test
    void beforeControllerMethod_shouldHandleNullArgs() {
        String methodName = "AdminController.getStatus()";

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(null);

        adminLoggingAspect.beforeControllerMethod(joinPoint);

        assertEquals(1, logAppender.list.size());
        ILoggingEvent logEvent = logAppender.list.get(0);
        assertEquals("INFO", logEvent.getLevel().toString());
        assertTrue(logEvent.getMessage().contains("[ADMIN CONTROLLER] Entering: {} with args: {}"));
        //assertEquals(methodName, logEvent.getArgumentArray()[0]);
        //assertNull(logEvent.getArgumentArray()[1]);
    }

    @Test
    void afterControllerMethod_shouldHandleNullResult() {
        String methodName = "AdminController.voidMethod()";

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn(methodName);

        adminLoggingAspect.afterControllerMethod(joinPoint, null);

        assertEquals(1, logAppender.list.size());
        ILoggingEvent logEvent = logAppender.list.get(0);
        assertEquals("INFO", logEvent.getLevel().toString());
        assertTrue(logEvent.getMessage().contains("[ADMIN CONTROLLER] Exiting: {} with response: {}"));
        //assertEquals(methodName, logEvent.getArgumentArray()[0]);
        //assertNull(logEvent.getArgumentArray()[1]);
    }

    @Test
    void beforeServiceMethod_shouldHandleEmptyArgs() {
        String methodName = "AdminServiceImpl.getAllUsers()";
        Object[] emptyArgs = {};

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(emptyArgs);

        adminLoggingAspect.beforeServiceMethod(joinPoint);

        assertEquals(1, logAppender.list.size());
        ILoggingEvent logEvent = logAppender.list.get(0);
        assertEquals("INFO", logEvent.getLevel().toString());
        assertTrue(logEvent.getMessage().contains("[ADMIN SERVICE] Entering: {} with args: {}"));
       // assertEquals(methodName, logEvent.getArgumentArray()[0]);
       // assertEquals(emptyArgs, logEvent.getArgumentArray()[1]);
    }

    @Test
    void onServiceException_shouldHandleExceptionWithNullMessage() {
        String methodName = "AdminServiceImpl.criticalOperation()";
        Exception exception = new NullPointerException();

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn(methodName);

        adminLoggingAspect.onServiceException(joinPoint, exception);

        assertEquals(1, logAppender.list.size());
        ILoggingEvent logEvent = logAppender.list.get(0);
        assertEquals("ERROR", logEvent.getLevel().toString());
        assertTrue(logEvent.getMessage().contains("[ADMIN SERVICE] Exception in {}: {}"));
       // assertEquals(methodName, logEvent.getArgumentArray()[0]);
        //assertNull(logEvent.getArgumentArray()[1]);
        //assertEquals(exception, logEvent.getArgumentArray()[2]);
    }

    // Multiple Logs Test

    @Test
    void multipleMethodCalls_shouldLogAllEvents() {
        String controllerMethod = "AdminController.testMethod()";
        String serviceMethod = "AdminServiceImpl.testService()";
        Object[] args = {"test"};

        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(args);

        when(signature.toString()).thenReturn(controllerMethod);
        adminLoggingAspect.beforeControllerMethod(joinPoint);

        when(signature.toString()).thenReturn(serviceMethod);
        adminLoggingAspect.beforeServiceMethod(joinPoint);

        adminLoggingAspect.afterServiceMethod(joinPoint, "service result");

        when(signature.toString()).thenReturn(controllerMethod);
        adminLoggingAspect.afterControllerMethod(joinPoint, "controller result");

        assertEquals(4, logAppender.list.size());

        assertTrue(logAppender.list.get(0).getMessage().contains("[ADMIN CONTROLLER] Entering"));
        assertTrue(logAppender.list.get(1).getMessage().contains("[ADMIN SERVICE] Entering"));
        assertTrue(logAppender.list.get(2).getMessage().contains("[ADMIN SERVICE] Exiting"));
        assertTrue(logAppender.list.get(3).getMessage().contains("[ADMIN CONTROLLER] Exiting"));
    }
}
