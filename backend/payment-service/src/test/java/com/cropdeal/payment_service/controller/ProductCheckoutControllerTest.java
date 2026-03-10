package com.cropdeal.payment_service.controller;

import com.cropdeal.payment_service.dto.ProductRequest;
import com.cropdeal.payment_service.dto.StripeResponse;
import com.cropdeal.payment_service.service.StripeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductCheckoutControllerTest {

    @Mock
    private StripeService stripeService;

    @InjectMocks
    private ProductCheckoutController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckoutProducts() {
        ProductRequest request = new ProductRequest();
        request.setAmount(100L);
        request.setQuantity(2L);
        request.setName("Test Product");
        request.setCurrency("USD");

        StripeResponse response = new StripeResponse("SUCCESS", "Payment session created", "sess_123", "http://session.url");

        when(stripeService.checkoutProducts(request)).thenReturn(response);

        ResponseEntity<StripeResponse> entity = controller.checkoutProducts(request);

        assertEquals(200, entity.getStatusCodeValue());
        assertEquals("SUCCESS", entity.getBody().getStatus());
        assertEquals("sess_123", entity.getBody().getSessionId());
        verify(stripeService, times(1)).checkoutProducts(request);
    }
}
