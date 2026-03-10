package com.cropdeal.chatbot.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Logger logger;

    @Mock
    private Signature signature;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject the mock logger into the aspect via reflection since logger is final and private
        try {
            var field = LoggingAspect.class.getDeclaredField("logger");
            field.setAccessible(true);
            field.set(loggingAspect, logger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testLogAround_success() throws Throwable {
        String methodName = "SomeClass.someMethod()";
        Object[] args = new Object[]{"arg1", 2};
        Object result = "result";

        // Properly mock signature
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);

        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenReturn(result);

        Object returned = loggingAspect.logAround(joinPoint);

        assertEquals(result, returned);

        // Verify debug logs
        verify(logger).debug("Entering method: {} with arguments: {}", methodName, args);
        verify(logger).debug("Exiting method: {} with result: {}", methodName, result);
        verify(logger).debug(startsWith("Execution time: "), anyLong());
    }

    @Test
    void testLogAround_proceedThrows() throws Throwable {
        String methodName = "SomeClass.someMethod()";
        Object[] args = new Object[]{"arg1", 2};

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);

        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenThrow(new RuntimeException("fail"));

        assertThrows(RuntimeException.class, () -> loggingAspect.logAround(joinPoint));

        verify(logger).debug("Entering method: {} with arguments: {}", methodName, args);}
        // Exiting and Execution time logs will NOT be called because proceed threw exception
        //verify(logger, never()).debug("Exiting method: {} with result: {}", any(), any());
        //verify(logger, never()).debug(startsWith("Execution time: "), anyLong());
    
}
