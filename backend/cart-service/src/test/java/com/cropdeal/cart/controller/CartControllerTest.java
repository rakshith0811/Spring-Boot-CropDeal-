package com.cropdeal.cart.controller;

import com.cropdeal.cart.dto.AddToCartRequest;
import com.cropdeal.cart.dto.CropInfoDTO;
import com.cropdeal.cart.entity.CartItem;
import com.cropdeal.cart.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class) // Focuses on testing only the CartController
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform HTTP requests in tests

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON strings

    @MockBean // Mocks the CartService dependency
    private CartService cartService;

    private final Long DEALER_ID = 1L;
    private final Long CROP_ID = 101L;
    private final Integer QUANTITY = 5;
    private final Integer FARMER_ID = 201;

    /**
     * Test case for successful addition of an item to the cart.
     * Verifies that the POST request to /api/cart/add/{dealerId} returns 200 OK
     * and the expected CartItem object.
     */
    @Test
    void addToCart_Success() throws Exception {
        // Create a request DTO
        AddToCartRequest request = new AddToCartRequest();
        request.setCropId(CROP_ID);
        request.setQuantity(QUANTITY);
        request.setFarmerId(FARMER_ID);

        // Create a mock CartItem that would be returned by the service
        CartItem mockCartItem = CartItem.builder()
                .id(1L)
                .dealerId(DEALER_ID)
                .cropId(CROP_ID)
                .quantity(QUANTITY)
                .farmerId(FARMER_ID)
                .cropName("Wheat")
                .price(100.0)
                .totalPrice(500.0)
                .build();

        // Configure the mock service to return the mock CartItem when addToCart is called
        when(cartService.addToCart(eq(DEALER_ID), eq(CROP_ID), eq(QUANTITY), eq(FARMER_ID)))
                .thenReturn(mockCartItem);

        // Perform the POST request and assert the results
        mockMvc.perform(post("/api/cart/add/{dealerId}", DEALER_ID)
                .contentType(MediaType.APPLICATION_JSON) // Set request content type to JSON
                .content(objectMapper.writeValueAsString(request))) // Convert request object to JSON string
                .andExpect(status().isOk()) // Expect HTTP 200 OK status
                .andExpect(jsonPath("$.id").value(mockCartItem.getId())) // Verify specific fields in the JSON response
                .andExpect(jsonPath("$.cropName").value(mockCartItem.getCropName()));

        // Verify that the addToCart method on the service was called exactly once with the correct arguments
        verify(cartService, times(1)).addToCart(eq(DEALER_ID), eq(CROP_ID), eq(QUANTITY), eq(FARMER_ID));
    }

    /**
     * Test case for adding an item to cart when dealer is not found.
     * Verifies that the controller handles IllegalArgumentException from service
     * and returns 400 Bad Request.
     */
        /**
     * Test case for adding an item to cart when crop quantity exceeds available stock.
     * Verifies that the controller handles IllegalArgumentException from service
     * and returns 400 Bad Request.
     */
   
    /**
     * Test case for retrieving cart items when the cart is not empty.
     * Verifies that the GET request to /api/cart/{dealerId} returns 200 OK
     * and a list of CropInfoDTOs.
     */
    @Test
    void getCart_WithItems_ReturnsListOfCropInfoDTO() throws Exception {
        // Create mock CropInfoDTOs that the service would return
        List<CropInfoDTO> mockCropInfoList = Arrays.asList(
                CropInfoDTO.builder().cartItemId(1L).cropId(CROP_ID).cropName("Wheat").quantity(5).build(),
                CropInfoDTO.builder().cartItemId(2L).cropId(102L).cropName("Rice").quantity(10).build()
        );

        // Configure the mock service to return the list of CropInfoDTOs
        when(cartService.getCartItems(DEALER_ID)).thenReturn(mockCropInfoList);

        // Perform the GET request and assert the results
        mockMvc.perform(get("/api/cart/{dealerId}", DEALER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.length()").value(2)) // Verify the list size
                .andExpect(jsonPath("$[0].cropName").value("Wheat")); // Verify a specific field in the first item

        // Verify that the getCartItems method on the service was called exactly once
        verify(cartService, times(1)).getCartItems(DEALER_ID);
    }

    /**
     * Test case for retrieving cart items when the cart is empty.
     * Verifies that the GET request returns 200 OK and an empty list.
     */
    @Test
    void getCart_EmptyCart_ReturnsEmptyList() throws Exception {
        // Configure the mock service to return an empty list
        when(cartService.getCartItems(DEALER_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/cart/{dealerId}", DEALER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0)); // Verify the list is empty

        verify(cartService, times(1)).getCartItems(DEALER_ID);
    }

    /**
     * Test case for successfully clearing the cart.
     * Verifies that the DELETE request to /api/cart/clear/{dealerId} returns 200 OK
     * and the expected success message.
     */
    @Test
    void clearCart_Success() throws Exception {
        // Configure the mock service to do nothing when clearCart is called (void method)
        doNothing().when(cartService).clearCart(DEALER_ID);

        // Perform the DELETE request and assert the results
        mockMvc.perform(delete("/api/cart/clear/{dealerId}", DEALER_ID))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(content().string("Cart deleted successfully")); // Verify the response body

        // Verify that the clearCart method on the service was called exactly once
        verify(cartService, times(1)).clearCart(DEALER_ID);
    }
}
