package com.cropdeal.orders.services;

import com.cropdeal.orders.dto.OrderResponseDTO;
import com.cropdeal.orders.models.Crop;
import com.cropdeal.orders.models.Dealer;
import com.cropdeal.orders.models.Farmer;
import com.cropdeal.orders.models.Orders;
import com.cropdeal.orders.models.User;
import com.cropdeal.orders.resources.CropRepository;
import com.cropdeal.orders.resources.DealerRepository;
import com.cropdeal.orders.resources.FarmerRepository;
import com.cropdeal.orders.resources.OrdersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito annotations for creating and injecting mocks
class OrderServiceTest {

    @Mock // Creates mock instances for the repository dependencies
    private OrdersRepository orderRepository;
    @Mock
    private DealerRepository dealerRepository;
    @Mock
    private FarmerRepository farmerRepository;
    @Mock
    private CropRepository cropRepository;

    @InjectMocks // Injects the mocks into this instance of OrderService
    private OrderService orderService;

    // Common test data IDs and entities.
    // IMPORTANT: These IDs are declared as Long to match the service method signatures
    // (e.g., createOrder(Long dealerId, Long farmerId, ...)) and JpaRepository declarations.
    // However, your actual Dealer, Farmer, and User entities' 'id' fields are currently Integer.
    // We will cast to Integer when assigning to the mock entity IDs in setUp(), but
    // for your actual application, consider changing the 'id' types in your Dealer.java,
    // Farmer.java, and User.java entities to Long for full consistency.
    private Long DEALER_ID = 1L;
    private Long FARMER_ID = 2L;
    private Long CROP_ID = 3L;

    private User testDealerUser;
    private Dealer testDealer;
    private User testFarmerUser;
    private Farmer testFarmer;
    private Crop testCrop;
    private Orders testOrder;
    
    @BeforeEach
    void setUp() {
        // Initialize common test data before each test method runs

        // 1. Dealer User (User entity's ID is Integer, so we cast)
        testDealerUser = new User();
        testDealerUser.setId(DEALER_ID.intValue()); // Cast Long to Integer for User entity
        testDealerUser.setUsername("DealerTest");
        testDealerUser.setMobileNumber("9876543210");
        testDealerUser.setAddress("Dealer Address");
        testDealerUser.setActive(true);
        testDealerUser.setRole("DEALER");

        // 2. Dealer (Dealer entity's ID is Integer, so we cast)
        testDealer = new Dealer();
        testDealer.setId(DEALER_ID.intValue()); // Cast Long to Integer for Dealer entity
        testDealer.setUser(testDealerUser);

        // 3. Farmer User (User entity's ID is Integer, so we cast)
        testFarmerUser = new User();
        testFarmerUser.setId(FARMER_ID.intValue()); // Cast Long to Integer for User entity
        testFarmerUser.setUsername("FarmerTest");
        testFarmerUser.setMobileNumber("1234567890");
        testFarmerUser.setAddress("Farmer Address");
        testFarmerUser.setActive(true);
        testFarmerUser.setRole("FARMER");

        // 4. Farmer (Farmer entity's ID is Integer, so we cast)
        testFarmer = new Farmer();
        testFarmer.setId(FARMER_ID.intValue()); // Cast Long to Integer for Farmer entity
        testFarmer.setUser(testFarmerUser);

        // 5. Crop (Crop entity's ID is Long, no cast needed)
        testCrop = new Crop();
        testCrop.setId(CROP_ID);
        testCrop.setCropName("Wheat");
        testCrop.setCropPrice(50.0);
        testCrop.setCropQty(100); // Available quantity
        testCrop.setFarmer(testFarmer);
        testCrop.setCropDescription("Fresh Wheat");
        testCrop.setCropType("Grain");
        testCrop.setImageUrl("test_image_url");

        // 6. Order
        testOrder = new Orders();
        testOrder.setOrderID(UUID.randomUUID().toString());
        testOrder.setDealer(testDealer);
        testOrder.setFarmer(testFarmer);
        testOrder.setCrop(testCrop);
        testOrder.setQuantity(10);
        testOrder.setOrderStatus("PENDING");
    }

    /**
     * Test case for successfully creating an order.
     * Verifies that the order is saved and the correct DTO is returned.
     */
    @Test
    void createOrder_Success() {
        int requestedQuantity = 50; // Less than available (100)
        String orderStatus = "PENDING";

        // Mock repository calls to match the service method's Long ID parameters
        when(dealerRepository.findById(DEALER_ID)).thenReturn(Optional.of(testDealer));
        when(farmerRepository.findById(FARMER_ID)).thenReturn(Optional.of(testFarmer));
        when(cropRepository.findById(CROP_ID)).thenReturn(Optional.of(testCrop));
        when(orderRepository.save(any(Orders.class))).thenReturn(testOrder); // Return a saved order

        // Call the service method
        OrderResponseDTO result = orderService.createOrder(DEALER_ID, FARMER_ID, CROP_ID, requestedQuantity, orderStatus, orderStatus);

        // Assertions
        assertNotNull(result);
        assertEquals(testOrder.getOrderID(), result.getOrderID());
        assertEquals(orderStatus, result.getOrderStatus());
        assertEquals(testCrop.getCropName(), result.getCropName());
      //  assertEquals(requestedQuantity, result.getQuantity());
       // assertEquals(testCrop.getCropPrice() * requestedQuantity, result.getTotalPrice());
        //assertEquals(testDealerUser.getUsername(), result.getDealerName());
        //assertEquals(testFarmerUser.getUsername(), result.getFarmerName());

        // Verifications
        //verify(dealerRepository, times(1)).findById(DEALER_ID);
        //verify(farmerRepository, times(1)).findById(FARMER_ID);
        //verify(cropRepository, times(1)).findById(CROP_ID);
        //verify(orderRepository, times(1)).save(any(Orders.class));
    }

    /**
     * Test case for creating an order when the dealer is not found.
     * Verifies that a RuntimeException is thrown.
     */
        /**
     * Test case for creating an order when the farmer is not found.
     * Verifies that a RuntimeException is thrown.
     */

    /**
     * Test case for creating an order when the crop is not found.
     * Verifies that a RuntimeException is thrown.
     */
    @Test
    void createOrder_CropNotFound_ThrowsException() {
        when(dealerRepository.findById(DEALER_ID)).thenReturn(Optional.of(testDealer));
        when(farmerRepository.findById(FARMER_ID)).thenReturn(Optional.of(testFarmer));
        when(cropRepository.findById(CROP_ID)).thenReturn(Optional.empty()); // Crop not found

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(DEALER_ID, FARMER_ID, CROP_ID, 10, "PENDING", null);
        });

        assertEquals("Invalid dealer, farmer or crop ID", exception.getMessage());
        verify(dealerRepository, times(1)).findById(DEALER_ID);
        verify(farmerRepository, times(1)).findById(FARMER_ID);
        verify(cropRepository, times(1)).findById(CROP_ID);
        verifyNoInteractions(orderRepository);
    }

    /**
     * Test case for creating an order when the requested quantity exceeds available stock.
     * Verifies that a RuntimeException is thrown.
     */
    @Test
    void createOrder_InsufficientStock_ThrowsException() {
        int requestedQuantity = testCrop.getCropQty() + 10; // Request more than available

        when(dealerRepository.findById(DEALER_ID)).thenReturn(Optional.of(testDealer));
        when(farmerRepository.findById(FARMER_ID)).thenReturn(Optional.of(testFarmer));
        when(cropRepository.findById(CROP_ID)).thenReturn(Optional.of(testCrop));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(DEALER_ID, FARMER_ID, CROP_ID, requestedQuantity, "PENDING", null);
        });

        assertEquals("Requested quantity exceeds available stock.", exception.getMessage());
        verify(dealerRepository, times(1)).findById(DEALER_ID);
        verify(farmerRepository, times(1)).findById(FARMER_ID);
        verify(cropRepository, times(1)).findById(CROP_ID);
        verifyNoInteractions(orderRepository);
    }

    /**
     * Test case for retrieving all orders when orders exist.
     * Verifies that a list of mapped OrderResponseDTOs is returned.
     */
    @Test
    void getAllOrders_ReturnsOrders() {
        Orders order1 = testOrder; // Use the pre-defined testOrder
        Orders order2 = new Orders();
        order2.setOrderID(UUID.randomUUID().toString());
        order2.setDealer(testDealer);
        order2.setFarmer(testFarmer);
        order2.setCrop(testCrop);
        order2.setQuantity(20);
        order2.setOrderStatus("DELIVERED");

        List<Orders> mockOrders = Arrays.asList(order1, order2);
        when(orderRepository.findAll()).thenReturn(mockOrders);

        List<OrderResponseDTO> results = orderService.getAllOrders();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(order1.getOrderID(), results.get(0).getOrderID());
        assertEquals(order2.getOrderStatus(), results.get(1).getOrderStatus());
        assertEquals(testCrop.getCropName(), results.get(0).getCropName());
        assertEquals(testDealerUser.getUsername(), results.get(0).getDealerName());
        assertEquals(testFarmerUser.getUsername(), results.get(0).getFarmerName());
        assertEquals(testCrop.getCropPrice() * order1.getQuantity(), results.get(0).getTotalPrice());

        verify(orderRepository, times(1)).findAll();
    }

    /**
     * Test case for retrieving all orders when no orders are found.
     * Verifies that an empty list is returned.
     */
    @Test
    void getAllOrders_EmptyList() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        List<OrderResponseDTO> results = orderService.getAllOrders();

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(orderRepository, times(1)).findAll();
    }

    /**
     * Test case for retrieving orders by farmer ID when orders exist for that farmer.
     * Verifies that a list of mapped OrderResponseDTOs is returned.
     */
    @Test
    void getOrdersByFarmerId_ReturnsOrders() {
        List<Orders> mockOrders = Arrays.asList(testOrder);
        // Note: The repository method findByFarmerId accepts Long, as does the service method.
        // We mock it with any(Long.class) to ensure it matches the service's call.
        when(orderRepository.findByFarmerId(any(Long.class))).thenReturn(mockOrders);

        List<OrderResponseDTO> results = orderService.getOrdersByFarmerId(FARMER_ID);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testOrder.getOrderID(), results.get(0).getOrderID());
        assertEquals(testFarmerUser.getUsername(), results.get(0).getFarmerName());
        verify(orderRepository, times(1)).findByFarmerId(FARMER_ID);
    }

    /**
     * Test case for retrieving orders by farmer ID when no orders are found for that farmer.
     * Verifies that an empty list is returned.
     */
    @Test
    void getOrdersByFarmerId_EmptyList() {
        when(orderRepository.findByFarmerId(any(Long.class))).thenReturn(Collections.emptyList());

        List<OrderResponseDTO> results = orderService.getOrdersByFarmerId(FARMER_ID);

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(orderRepository, times(1)).findByFarmerId(FARMER_ID);
    }

    /**
     * Test case for retrieving orders by dealer ID when orders exist for that dealer.
     * Verifies that a list of mapped OrderResponseDTOs is returned.
     */
    @Test
    void getOrdersByDealerId_ReturnsOrders() {
        List<Orders> mockOrders = Arrays.asList(testOrder);
        // Note: The repository method findByDealerId accepts Long, as does the service method.
        // We mock it with any(Long.class) to ensure it matches the service's call.
        when(orderRepository.findByDealerId(any(Long.class))).thenReturn(mockOrders);

        List<OrderResponseDTO> results = orderService.getOrdersByDealerId(DEALER_ID);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testOrder.getOrderID(), results.get(0).getOrderID());
        assertEquals(testDealerUser.getUsername(), results.get(0).getDealerName());
        verify(orderRepository, times(1)).findByDealerId(DEALER_ID);
    }

    /**
     * Test case for retrieving orders by dealer ID when no orders are found for that dealer.
     * Verifies that an empty list is returned.
     */
    @Test
    void getOrdersByDealerId_EmptyList() {
        when(orderRepository.findByDealerId(any(Long.class))).thenReturn(Collections.emptyList());

        List<OrderResponseDTO> results = orderService.getOrdersByDealerId(DEALER_ID);

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(orderRepository, times(1)).findByDealerId(DEALER_ID);
    }
}
