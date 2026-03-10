package com.cropdeal.payment_service.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class LoggingAspectTest {

    private LoggingAspect loggingAspect;
    private JoinPoint joinPoint;
    private Signature signature;

    @BeforeEach
    void setUp() {
        loggingAspect = new LoggingAspect();
        joinPoint = mock(JoinPoint.class);
        signature = mock(Signature.class);

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn("signature");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", 123});
    }

    @Test
    void testLogBeforeController() {
        loggingAspect.logBeforeController(joinPoint);
    }

    @Test
    void testLogAfterController() {
        loggingAspect.logAfterController(joinPoint, "result");
    }

    @Test
    void testLogBeforeService() {
        loggingAspect.logBeforeService(joinPoint);
    }

    @Test
    void testLogAfterService() {
        loggingAspect.logAfterService(joinPoint, "result");
    }
}
