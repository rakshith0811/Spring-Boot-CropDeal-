package com.cropdeal.cart.service;

import com.cropdeal.cart.dto.CropInfoDTO;
import com.cropdeal.cart.entity.CartItem;
import com.cropdeal.cart.entity.Crop;
import com.cropdeal.cart.entity.Dealer;
import com.cropdeal.cart.entity.Farmer;
import com.cropdeal.cart.entity.User;
import com.cropdeal.cart.repository.CartItemRepository;
import com.cropdeal.cart.repository.CropRepository;
import com.cropdeal.cart.repository.DealerRepository;
import com.cropdeal.cart.repository.FarmerRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito annotations
class CartServiceTest {

    @Mock // Creates mock instances of the repositories
    private CartItemRepository cartRepo;
    @Mock
    private CropRepository cropRepo;
    @Mock
    private DealerRepository dealerRepo;
    @Mock
    private FarmerRepository farmerRepo;

    @InjectMocks // Injects the mocks into the CartService instance
    private CartService cartService;

    // Test data
    private Long DEALER_ID;
    private Long CROP_ID;
    private Integer QUANTITY;
    private Integer FARMER_ID;
    private Crop testCrop;
    private Dealer testDealer;
    private Farmer testFarmer;
    private User testFarmerUser;

    @BeforeEach
    void setUp() {
        // Initialize test data before each test
        DEALER_ID = 1L;
        CROP_ID = 101L;
        QUANTITY = 5;
        FARMER_ID = 201;

        // Mock Crop entity
        testCrop = new Crop();
        testCrop.setId(CROP_ID);
        testCrop.setCropName("Test Wheat");
        testCrop.setCropPrice(150.0);
        testCrop.setCropQty(10); // Available stock
        testCrop.setImageUrl("http://example.com/wheat.jpg");

        // Mock Dealer entity
        testDealer = new Dealer();
        testDealer.setId(DEALER_ID.intValue()); // Assuming Dealer ID is Integer
        testDealer.setUserId(DEALER_ID.intValue());

        // Mock Farmer User entity
        testFarmerUser = new User();
        testFarmerUser.setId(FARMER_ID);
        testFarmerUser.setUsername("farmerJohn");

        // Mock Farmer entity
        testFarmer = new Farmer();
        testFarmer.setId(FARMER_ID);
        testFarmer.setUser(testFarmerUser); // Link farmer to user
    }

    /**
     * Test case for successful addition of a new item to the cart.
     * Verifies that repositories are called correctly and a CartItem is returned.
     */
    @Test
    void addToCart_Success() {
        // Configure mocks to return expected data
        when(dealerRepo.existsById(DEALER_ID.intValue())).thenReturn(true);
        when(cropRepo.findById(CROP_ID)).thenReturn(Optional.of(testCrop));
        when(farmerRepo.findById(FARMER_ID)).thenReturn(Optional.of(testFarmer));

        // Mock the save operation of cartRepo
        CartItem expectedCartItem = CartItem.builder()
                .dealerId(DEALER_ID)
                .cropId(CROP_ID)
                .cropName(testCrop.getCropName())
                .imageUrl(testCrop.getImageUrl())
                .quantity(QUANTITY)
                .price(testCrop.getCropPrice())
                .totalPrice(QUANTITY * testCrop.getCropPrice())
                .farmerId(FARMER_ID)
                .farmerName(testFarmerUser.getUsername())
                .build();
        when(cartRepo.save(any(CartItem.class))).thenReturn(expectedCartItem);

        // Call the service method
        CartItem result = cartService.addToCart(DEALER_ID, CROP_ID, QUANTITY, FARMER_ID);

        // Assertions
        assertNotNull(result);
        assertEquals(DEALER_ID, result.getDealerId());
        assertEquals(CROP_ID, result.getCropId());
        assertEquals(QUANTITY, result.getQuantity());
        assertEquals(testCrop.getCropName(), result.getCropName());
        assertEquals(testFarmerUser.getUsername(), result.getFarmerName());
        assertEquals(expectedCartItem.getTotalPrice(), result.getTotalPrice());

        // Verify repository interactions
        verify(dealerRepo, times(1)).existsById(DEALER_ID.intValue());
        verify(cropRepo, times(1)).findById(CROP_ID);
        verify(farmerRepo, times(1)).findById(FARMER_ID);
        verify(cartRepo, times(1)).save(any(CartItem.class));
    }

    /**
     * Test case for adding item when dealer does not exist.
     * Expects an IllegalArgumentException.
     */
    @Test
    void addToCart_DealerNotFound_ThrowsException() {
        when(dealerRepo.existsById(DEALER_ID.intValue())).thenReturn(false);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            cartService.addToCart(DEALER_ID, CROP_ID, QUANTITY, FARMER_ID);
        });

        assertEquals("Dealer not found with id: " + DEALER_ID, thrown.getMessage());
        verify(dealerRepo, times(1)).existsById(DEALER_ID.intValue());
        verifyNoInteractions(cropRepo, farmerRepo, cartRepo); // No other interactions should occur
    }

    /**
     * Test case for adding item when crop does not exist.
     * Expects a RuntimeException.
     */
    @Test
    void addToCart_CropNotFound_ThrowsException() {
        when(dealerRepo.existsById(DEALER_ID.intValue())).thenReturn(true);
        when(cropRepo.findById(CROP_ID)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            cartService.addToCart(DEALER_ID, CROP_ID, QUANTITY, FARMER_ID);
        });

        assertEquals("Crop not found with id: " + CROP_ID, thrown.getMessage());
        verify(dealerRepo, times(1)).existsById(DEALER_ID.intValue());
        verify(cropRepo, times(1)).findById(CROP_ID);
        verifyNoInteractions(farmerRepo, cartRepo);
    }

    /**
     * Test case for adding item when farmer does not exist.
     * Expects a RuntimeException.
     */
    @Test
    void addToCart_FarmerNotFound_ThrowsException() {
        when(dealerRepo.existsById(DEALER_ID.intValue())).thenReturn(true);
        when(cropRepo.findById(CROP_ID)).thenReturn(Optional.of(testCrop));
        when(farmerRepo.findById(FARMER_ID)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            cartService.addToCart(DEALER_ID, CROP_ID, QUANTITY, FARMER_ID);
        });

        assertEquals("Farmer not found with ID: " + FARMER_ID + ".", thrown.getMessage());
        verify(dealerRepo, times(1)).existsById(DEALER_ID.intValue());
        verify(cropRepo, times(1)).findById(CROP_ID);
        verify(farmerRepo, times(1)).findById(FARMER_ID);
        verifyNoInteractions(cartRepo);
    }

    /**
     * Test case for adding item when requested quantity exceeds available stock.
     * Expects an IllegalArgumentException.
     */
    @Test
    void addToCart_QuantityExceedsStock_ThrowsException() {
        testCrop.setCropQty(QUANTITY - 1); // Set available stock less than requested quantity

        when(dealerRepo.existsById(DEALER_ID.intValue())).thenReturn(true);
        when(cropRepo.findById(CROP_ID)).thenReturn(Optional.of(testCrop));
        when(farmerRepo.findById(FARMER_ID)).thenReturn(Optional.of(testFarmer));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            cartService.addToCart(DEALER_ID, CROP_ID, QUANTITY, FARMER_ID);
        });

        assertEquals("Requested quantity exceeds available stock (" + testCrop.getCropQty() + " units).", thrown.getMessage());
        verify(dealerRepo, times(1)).existsById(DEALER_ID.intValue());
        verify(cropRepo, times(1)).findById(CROP_ID);
        verify(farmerRepo, times(1)).findById(FARMER_ID);
        verifyNoInteractions(cartRepo);
    }

    /**
     * Test case for getting cart items when the cart has items.
     * Verifies that the service returns a correctly mapped list of CropInfoDTOs.
     */
    @Test
    void getCartItems_WithItems_ReturnsListOfCropInfoDTO() {
        // Prepare mock CartItems
        CartItem item1 = CartItem.builder().id(1L).dealerId(DEALER_ID).cropId(101L).cropName("Wheat").quantity(5).price(100.0).totalPrice(500.0).farmerId(FARMER_ID).farmerName("farmerJohn").imageUrl("url1").build();
        CartItem item2 = CartItem.builder().id(2L).dealerId(DEALER_ID).cropId(102L).cropName("Rice").quantity(10).price(50.0).totalPrice(500.0).farmerId(FARMER_ID).farmerName("farmerJohn").imageUrl("url2").build();
        List<CartItem> mockCartItems = Arrays.asList(item1, item2);

        // Configure mock repository
        when(cartRepo.findByDealerId(DEALER_ID)).thenReturn(mockCartItems);

        // Call service method
        List<CropInfoDTO> result = cartService.getCartItems(DEALER_ID);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Wheat", result.get(0).getCropName());
        assertEquals(500.0, result.get(0).getTotalPrice());
        assertEquals(10, result.get(1).getQuantity());
        assertEquals(FARMER_ID, result.get(0).getFarmerId());
        assertEquals("farmerJohn", result.get(0).getFarmerName());

        // Verify repository interaction
        verify(cartRepo, times(1)).findByDealerId(DEALER_ID);
    }

    /**
     * Test case for getting cart items when the cart is empty.
     * Verifies that the service returns an empty list.
     */
    @Test
    void getCartItems_EmptyCart_ReturnsEmptyList() {
        // Configure mock repository to return an empty list
        when(cartRepo.findByDealerId(DEALER_ID)).thenReturn(Collections.emptyList());

        // Call service method
        List<CropInfoDTO> result = cartService.getCartItems(DEALER_ID);

        // Assertions
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify repository interaction
        verify(cartRepo, times(1)).findByDealerId(DEALER_ID);
    }

    /**
     * Test case for successfully clearing the cart.
     * Verifies that the deleteByDealerId method of the repository is called.
     */
    @Test
    void clearCart_Success() {
        // Configure mock repository to do nothing when deleteByDealerId is called
        doNothing().when(cartRepo).deleteByDealerId(DEALER_ID);

        // Call service method
        cartService.clearCart(DEALER_ID);

        // Verify repository interaction
        verify(cartRepo, times(1)).deleteByDealerId(DEALER_ID);
    }
}
