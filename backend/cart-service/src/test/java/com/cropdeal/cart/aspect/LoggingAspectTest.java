package com.cropdeal.cart.aspect;

import com.cropdeal.cart.controller.CartController;
import com.cropdeal.cart.dto.AddToCartRequest;
import com.cropdeal.cart.service.CartService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoggingAspectTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private CartController cartController;

    @SpyBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

   
    @Test
    void testControllerAndServiceLogging_onGetCart() throws Exception {
        mockMvc.perform(get("/api/cart/1"))
                .andExpect(status().isOk());

        verify(cartController).getCart(Mockito.eq(1L));
        verify(cartService).getCartItems(1L);
    }

    @Test
    void testControllerAndServiceLogging_onClearCart() throws Exception {
        mockMvc.perform(delete("/api/cart/clear/1"))
                .andExpect(status().isOk());

        verify(cartController).clearCart(Mockito.eq(1L));
        verify(cartService).clearCart(1L);
    }
}
