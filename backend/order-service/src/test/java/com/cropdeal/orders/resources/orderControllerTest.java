package com.cropdeal.orders.resources;

import com.cropdeal.orders.dto.OrderRequest;
import com.cropdeal.orders.dto.OrderResponseDTO;
import com.cropdeal.orders.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(orderController.class) // Focuses on testing only the orderController
class orderControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform HTTP requests in tests

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to/from JSON

    @MockBean // Mocks the OrderService dependency
    private OrderService orderService;

    // Common test data IDs (using Long as per controller method signatures)
    private Long DEALER_ID = 1L;
    private Long FARMER_ID = 2L;
    private Long CROP_ID = 3L;
    private String ORDER_ID = UUID.randomUUID().toString(); // Unique UUID for order ID

    private OrderResponseDTO testOrderResponseDTO;

    @BeforeEach
    void setUp() {
        // Initialize a common OrderResponseDTO for successful test cases
        testOrderResponseDTO = OrderResponseDTO.builder()
                .orderID(ORDER_ID)
                .orderStatus("PENDING")
                .cropName("Wheat")
                .cropPrice(10.0)
                .cropQty(100)
                .quantity(10)
                .farmerName("Farmer John")
                .farmerMobile("1112223333")
                .farmerAddress("Farm A")
                .dealerName("Dealer Jane")
                .dealerMobile("4445556666")
                .dealerAddress("Shop B")
                .totalPrice(100.0) // 10 * 10.0
                .build();
    }

    /**
     * Test case for successfully creating a new order.
     * Verifies that the POST request to /api/orders/addOrder returns 200 OK
     * and the expected OrderResponseDTO.
     */
    @Test
    void addOrder_Success() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setDealerId(DEALER_ID);
        orderRequest.setFarmerId(FARMER_ID);
        orderRequest.setCropId(CROP_ID);
        orderRequest.setQuantity(10);
        orderRequest.setOrderStatus("PENDING");

        // Mock the service call to return our predefined DTO
        when(orderService.createOrder(
                eq(DEALER_ID), eq(FARMER_ID), eq(CROP_ID), eq(10), eq("PENDING"), ORDER_ID))
                .thenReturn(testOrderResponseDTO);

        mockMvc.perform(post("/api/orders/addOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))) // Convert DTO to JSON string
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderID").value(ORDER_ID))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"))
                .andExpect(jsonPath("$.cropName").value("Wheat"))
                .andExpect(jsonPath("$.totalPrice").value(100.0)); // Asserting on totalPrice
    }

    /**
     * Test case for creating an order with invalid IDs (e.g., dealer, farmer, or crop not found).
     * Verifies that the controller handles RuntimeException and returns 400 Bad Request.
     */
       /**
     * Test case for creating an order with quantity exceeding available stock.
     * Verifies that the controller handles RuntimeException and returns 400 Bad Request.
     */
   
    /**
     * Test case for successfully retrieving all orders.
     * Verifies that the GET request to /api/orders/getAllOrder returns 200 OK
     * and a list of OrderResponseDTOs.
     */
    @Test
    void getAllOrder_Success() throws Exception {
        // Create a second DTO for the list
        OrderResponseDTO anotherOrder = OrderResponseDTO.builder()
                .orderID(UUID.randomUUID().toString())
                .orderStatus("DELIVERED")
                .cropName("Corn")
                .quantity(20)
                .build();
        List<OrderResponseDTO> mockOrders = Arrays.asList(testOrderResponseDTO, anotherOrder);

        when(orderService.getAllOrders()).thenReturn(mockOrders);

        mockMvc.perform(get("/api/orders/getAllOrder")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderID").value(ORDER_ID))
                .andExpect(jsonPath("$[1].orderStatus").value("DELIVERED"));
    }

    /**
     * Test case for retrieving all orders when no orders are available.
     * Verifies that the GET request returns 200 OK and an empty list.
     */
    @Test
    void getAllOrder_EmptyList() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders/getAllOrder")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Test case for successfully retrieving orders by farmer ID.
     * Verifies that the GET request to /api/orders/getOrderByFarmer/{farmerID} returns 200 OK
     * and a list of OrderResponseDTOs.
     */
    @Test
    void getOrderByFarmer_Success() throws Exception {
        List<OrderResponseDTO> ordersByFarmer = Arrays.asList(testOrderResponseDTO);
        when(orderService.getOrdersByFarmerId(FARMER_ID)).thenReturn(ordersByFarmer);

        mockMvc.perform(get("/api/orders/getOrderByFarmer/{farmerID}", FARMER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].farmerName").value("Farmer John"));
    }

    /**
     * Test case for retrieving orders by farmer ID when no orders are found for that farmer.
     * Verifies that the GET request returns 200 OK and an empty list.
     */
    @Test
    void getOrderByFarmer_NotFound() throws Exception {
        when(orderService.getOrdersByFarmerId(FARMER_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders/getOrderByFarmer/{farmerID}", FARMER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Test case for successfully retrieving orders by dealer ID.
     * Verifies that the GET request to /api/orders/getOrderByDealer/{dealerID} returns 200 OK
     * and a list of OrderResponseDTOs.
     */
    @Test
    void getOrderByDealer_Success() throws Exception {
        List<OrderResponseDTO> ordersByDealer = Arrays.asList(testOrderResponseDTO);
        when(orderService.getOrdersByDealerId(DEALER_ID)).thenReturn(ordersByDealer);

        mockMvc.perform(get("/api/orders/getOrderByDealer/{dealerID}", DEALER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].dealerName").value("Dealer Jane"));
    }

    /**
     * Test case for retrieving orders by dealer ID when no orders are found for that dealer.
     * Verifies that the GET request returns 200 OK and an empty list.
     */
    @Test
    void getOrderByDealer_NotFound() throws Exception {
        when(orderService.getOrdersByDealerId(DEALER_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders/getOrderByDealer/{dealerID}", DEALER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
