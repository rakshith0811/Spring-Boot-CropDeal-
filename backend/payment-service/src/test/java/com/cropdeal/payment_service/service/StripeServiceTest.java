package com.cropdeal.payment_service.service;

import com.cropdeal.payment_service.dto.ProductRequest;
import com.cropdeal.payment_service.dto.StripeResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class StripeServiceTest {

    private StripeService stripeService;

    @BeforeEach
    void setUp() {
        stripeService = new StripeService();
        // Inject dummy values for properties
        ReflectionTestUtils.setField(stripeService, "secretKey", "sk_test_key");
        ReflectionTestUtils.setField(stripeService, "successUrl", "http://success.url");
        ReflectionTestUtils.setField(stripeService, "cancelUrl", "http://cancel.url");

        stripeService.init();
    }

    @Test
    void testCheckoutProductsSuccess() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setAmount(100L);
        request.setQuantity(2L);
        request.setName("Test Product");
        request.setCurrency("usd");

        // Mock static method Session.create(...)
        try (MockedStatic<Session> sessionMock = Mockito.mockStatic(Session.class)) {
            Session mockSession = Mockito.mock(Session.class);
            Mockito.when(mockSession.getId()).thenReturn("sess_123");
            Mockito.when(mockSession.getUrl()).thenReturn("http://session.url");

            sessionMock.when(() -> Session.create(Mockito.any(SessionCreateParams.class))).thenReturn(mockSession);

            StripeResponse response = stripeService.checkoutProducts(request);

            assertEquals("SUCCESS", response.getStatus());
            assertEquals("Payment session created", response.getMessage());
            assertEquals("sess_123", response.getSessionId());
            assertEquals("http://session.url", response.getSessionUrl());
        }
    }

    @Test
    void testCheckoutProductsStripeException() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setAmount(100L);
        request.setQuantity(2L);
        request.setName("Test Product");
        request.setCurrency("usd");

        try (MockedStatic<Session> sessionMock = Mockito.mockStatic(Session.class)) {
            StripeException stripeException = Mockito.mock(StripeException.class);
            Mockito.when(stripeException.getMessage()).thenReturn("Stripe error");

            sessionMock.when(() -> Session.create(Mockito.any(SessionCreateParams.class)))
                    .thenThrow(stripeException);

            StripeResponse response = stripeService.checkoutProducts(request);

            assertEquals("FAILURE", response.getStatus());
            assertTrue(response.getMessage().contains("Failed to create payment session"));
            assertNull(response.getSessionId());
            assertNull(response.getSessionUrl());
        }
    }

    @Test
    void testCheckoutProductsGeneralException() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setAmount(100L);
        request.setQuantity(2L);
        request.setName("Test Product");
        request.setCurrency("usd");

        try (MockedStatic<Session> sessionMock = Mockito.mockStatic(Session.class)) {
            sessionMock.when(() -> Session.create(Mockito.any(SessionCreateParams.class)))
                    .thenThrow(new RuntimeException("Runtime error"));

            StripeResponse response = stripeService.checkoutProducts(request);

            assertEquals("FAILURE", response.getStatus());
            assertEquals("Unexpected error occurred", response.getMessage());
            assertNull(response.getSessionId());
            assertNull(response.getSessionUrl());
        }
    }
}
