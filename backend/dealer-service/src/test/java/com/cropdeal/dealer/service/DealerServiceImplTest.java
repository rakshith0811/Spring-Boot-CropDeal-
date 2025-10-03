package com.cropdeal.dealer.service;

import com.cropdeal.dealer.dto.CropPublicDTO;
import com.cropdeal.dealer.dto.DealerDTO;
import com.cropdeal.dealer.entity.Crop;
import com.cropdeal.dealer.entity.Dealer;
import com.cropdeal.dealer.entity.Farmer;
import com.cropdeal.dealer.entity.User;
import com.cropdeal.dealer.repository.CropRepository;
import com.cropdeal.dealer.repository.DealerRepository;
import jakarta.persistence.EntityManager;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito annotations for creating and injecting mocks
class DealerServiceTest {

    @Mock // Creates a mock instance of DealerRepository
    private DealerRepository dealerRepository;
    @Mock // Creates a mock instance of CropRepository
    private CropRepository cropRepository;
    @Mock // Creates a mock instance of EntityManager, used for transactional operations like merge
    private EntityManager entityManager;

    @InjectMocks // Injects the mocks (dealerRepository, cropRepository, entityManager) into this instance of DealerServiceImpl
    private DealerServiceImpl dealerService;

    // Test data for consistent use across tests
    private Long DEALER_ID = 1L;
    private Long CROP_ID = 101L;
    private Integer QUANTITY_TO_REDUCE = 5;

    private User testUser;
    private Dealer testDealer;
    private Crop testCrop;
    private Farmer testFarmer;
    private User testFarmerUser;

    @BeforeEach
    void setUp() {
        // Initialize common test data before each test method runs

        // User entity for the Dealer
        testUser = new User();
        testUser.setId(DEALER_ID); // User ID is Long
        testUser.setUsername("testDealer");
        testUser.setMobileNumber("1234567890");
        testUser.setAddress("123 Dealer St");
        testUser.setActive(true);
        testUser.setRole("DEALER");

        // Dealer entity linked to the testUser
        testDealer = new Dealer();
        testDealer.setId(DEALER_ID); // Dealer ID is Long
        testDealer.setUser(testUser); // Link the User entity to the Dealer

        // User entity for the Farmer
        testFarmerUser = new User();
        testFarmerUser.setId(201L); // Farmer User ID is Long
        testFarmerUser.setUsername("farmerBob");
        testFarmerUser.setMobileNumber("9876543210");
        testFarmerUser.setAddress("Farmer's Address");
        testFarmerUser.setActive(true);
        testFarmerUser.setRole("FARMER");

        // Farmer entity linked to the testFarmerUser
        testFarmer = new Farmer();
        testFarmer.setId(201L); // Farmer ID is Long
        testFarmer.setUser(testFarmerUser); // Link the User entity to the Farmer

        // Crop entity linked to the testFarmer
        testCrop = new Crop();
        testCrop.setId(CROP_ID); // Crop ID is Long
        testCrop.setCropName("Wheat");
        testCrop.setCropType("Grain");
        testCrop.setCropPrice(100.0);
        testCrop.setCropQty(15); // Initial quantity for testing reductions
        testCrop.setCropDescription("Freshly harvested wheat.");
        testCrop.setImageUrl("http://example.com/wheat.jpg");
        testCrop.setFarmer(testFarmer); // Link the Farmer entity to the Crop
    }

    /**
     * Test case for successful retrieval of a dealer's profile.
     * Verifies that the correct Dealer entity is returned.
     */
    @Test
    void getProfile_Success() {
        // Mock the findById call on the dealerRepository to return our testDealer
        when(dealerRepository.findById(DEALER_ID)).thenReturn(Optional.of(testDealer));

        // Call the service method
        Dealer result = dealerService.getProfile(DEALER_ID);

        // Assertions: Verify the returned dealer is not null and matches our test data
        assertNotNull(result);
        assertEquals(DEALER_ID, result.getId());
        assertEquals("testDealer", result.getUser().getUsername());

        // Verification: Ensure dealerRepository.findById was called exactly once with the correct ID
        verify(dealerRepository, times(1)).findById(DEALER_ID);
    }

    /**
     * Test case for retrieving a dealer profile that does not exist.
     * Verifies that a RuntimeException is thrown.
     */
    @Test
    void getProfile_NotFound_ThrowsException() {
        // Mock the findById call to return an empty Optional, simulating not found
        when(dealerRepository.findById(DEALER_ID)).thenReturn(Optional.empty());

        // Assert that a RuntimeException is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dealerService.getProfile(DEALER_ID);
        });

        // Verify the exception message
        assertEquals("Dealer not found with id: " + DEALER_ID, exception.getMessage());

        // Verification: Ensure findById was called once
        verify(dealerRepository, times(1)).findById(DEALER_ID);
    }

    /**
     * Test case for successfully updating a dealer's profile (mobile number and address).
     * Verifies that the User entity linked to the Dealer is merged with updated data.
     */
    @Test
    void updateProfile_Success() {
        // DTO with updated information
        DealerDTO dto = new DealerDTO();
        dto.setMobileNumber("9998887770");
        dto.setAddress("New Dealer Address");

        // Mock `findById` to return an existing dealer with its associated user
        when(dealerRepository.findById(DEALER_ID)).thenReturn(Optional.of(testDealer));
        // Mock `entityManager.merge` to return the updated user (it's called internally)
        when(entityManager.merge(any(User.class))).thenReturn(testUser);

        // Call the service method
        Dealer result = dealerService.updateProfile(DEALER_ID, dto);

        // Assertions: Verify the returned dealer's user has the updated info
        assertNotNull(result);
        assertEquals("New Dealer Address", result.getUser().getAddress());
        assertEquals("9998887770", result.getUser().getMobileNumber());

        // Verifications
        verify(dealerRepository, times(1)).findById(DEALER_ID);
        // Verify that `entityManager.merge` was called with the `User` object
        verify(entityManager, times(1)).merge(testUser);
    }

    /**
     * Test case for updating a dealer profile that does not exist.
     * Verifies that a RuntimeException is thrown.
     */
    @Test
    void updateProfile_NotFound_ThrowsException() {
        DealerDTO dto = new DealerDTO();
        dto.setMobileNumber("999");
        dto.setAddress("abc");

        // Mock `findById` to return empty Optional
        when(dealerRepository.findById(DEALER_ID)).thenReturn(Optional.empty());

        // Assert that a RuntimeException is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dealerService.updateProfile(DEALER_ID, dto);
        });

        // Verify the exception message
        assertEquals("Dealer not found with id: " + DEALER_ID, exception.getMessage());

        // Verifications: Only `findById` should be called, no interaction with `entityManager`
        verify(dealerRepository, times(1)).findById(DEALER_ID);
        verifyNoInteractions(entityManager);
    }

    /**
     * Test case for viewing all crops with farmer information.
     * Verifies that the service maps Crop entities to CropPublicDTOs correctly.
     */
    @Test
    void viewAllCropsWithFarmerInfo_ReturnsListOfDTOs() {
        // Mock a list of crops that the cropRepository would return
        List<Crop> mockCrops = Arrays.asList(testCrop);
        when(cropRepository.findAllWithFarmerAndUser()).thenReturn(mockCrops);

        // Call the service method
        List<CropPublicDTO> result = dealerService.viewAllCropsWithFarmerInfo();

        // Assertions: Verify the list is not empty, size, and content
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Wheat", result.get(0).getCropName());
        assertEquals("farmerBob", result.get(0).getFarmerName());
        assertEquals(testCrop.getCropQty(), result.get(0).getCropQty()); // Verify cropQty mapping

        // Verification: Ensure cropRepository.findAllWithFarmerAndUser was called once
        verify(cropRepository, times(1)).findAllWithFarmerAndUser();
    }

    /**
     * Test case for viewing all crops when no crops are available.
     * Verifies that an empty list is returned.
     */
    @Test
    void viewAllCropsWithFarmerInfo_EmptyList() {
        // Mock an empty list of crops
        when(cropRepository.findAllWithFarmerAndUser()).thenReturn(Collections.emptyList());

        // Call the service method
        List<CropPublicDTO> result = dealerService.viewAllCropsWithFarmerInfo();

        // Assertions: Verify the list is empty
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verification: Ensure cropRepository.findAllWithFarmerAndUser was called once
        verify(cropRepository, times(1)).findAllWithFarmerAndUser();
    }

    /**
     * Test case for successfully retrieving all dealers.
     * Verifies that the service returns a list of Dealer entities.
     */
    @Test
    void getAllDealers_ReturnsListOfDealers() {
        // Mock a list of dealers
        List<Dealer> mockDealers = Arrays.asList(testDealer);
        when(dealerRepository.findAll()).thenReturn(mockDealers);

        // Call the service method
        List<Dealer> result = dealerService.getAllDealers();

        // Assertions: Verify the list is not empty, size, and content
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(DEALER_ID, result.get(0).getId());

        // Verification: Ensure dealerRepository.findAll was called once
        verify(dealerRepository, times(1)).findAll();
    }

    /**
     * Test case for retrieving all dealers when no dealers are available.
     * Verifies that an empty list is returned.
     */
    @Test
    void getAllDealers_EmptyList() {
        // Mock an empty list of dealers
        when(dealerRepository.findAll()).thenReturn(Collections.emptyList());

        // Call the service method
        List<Dealer> result = dealerService.getAllDealers();

        // Assertions: Verify the list is empty
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verification: Ensure dealerRepository.findAll was called once
        verify(dealerRepository, times(1)).findAll();
    }

    /**
     * Test case for successfully reducing crop quantity where the crop remains after reduction.
     * Verifies that the quantity is updated and a CropPublicDTO is returned.
     */
    @Test
    void reduceCropQuantity_Success_CropRemains() {
        Integer initialCropQty = testCrop.getCropQty(); // 15
        Integer remainingQty = initialCropQty - QUANTITY_TO_REDUCE; // 10

        // Mock `findById` to return the crop
        when(cropRepository.findById(CROP_ID)).thenReturn(Optional.of(testCrop));
        // Mock `reduceCropQuantity` to simulate a successful update
        when(cropRepository.reduceCropQuantity(CROP_ID, QUANTITY_TO_REDUCE)).thenReturn(1);

        // Create a mock Crop representing the state *after* the update query but *before* re-fetching
        // This is what findByIdWithFarmerAndUser should return
        Crop updatedCropEntity = new Crop();
        updatedCropEntity.setId(CROP_ID);
        updatedCropEntity.setCropName("Wheat");
        updatedCropEntity.setCropType("Grain");
        updatedCropEntity.setCropPrice(100.0);
        updatedCropEntity.setCropQty(remainingQty); // Set the updated quantity
        updatedCropEntity.setCropDescription("Freshly harvested wheat.");
        updatedCropEntity.setImageUrl("http://example.com/wheat.jpg");
        updatedCropEntity.setFarmer(testFarmer);

        when(cropRepository.findByIdWithFarmerAndUser(CROP_ID)).thenReturn(Optional.of(updatedCropEntity));

        // Call the service method
        CropPublicDTO result = dealerService.reduceCropQuantity(CROP_ID, QUANTITY_TO_REDUCE);

        // Assertions
        assertNotNull(result);
        assertEquals(CROP_ID, result.getId());
        assertEquals(remainingQty, result.getCropQty()); // Verify the quantity is reduced

        // Verifications
        verify(cropRepository, times(1)).findById(CROP_ID);
        verify(cropRepository, times(1)).reduceCropQuantity(CROP_ID, QUANTITY_TO_REDUCE);
        verify(cropRepository, times(1)).findByIdWithFarmerAndUser(CROP_ID);
        verify(cropRepository, never()).delete(any(Crop.class)); // Ensure deletion is not triggered
    }

    /**
     * Test case for reducing crop quantity where the crop quantity drops to 0 or less, leading to deletion.
     * Verifies that `null` is returned and the deletion method is called.
     */
    @Test
    void reduceCropQuantity_Success_CropDeleted() {
        Integer initialCropQty = 5;
        Integer quantityToReduce = 5; // Quantity will become 0
        testCrop.setCropQty(initialCropQty);

        // Mock `findById` to return the crop
        when(cropRepository.findById(CROP_ID)).thenReturn(Optional.of(testCrop));
        // Mock `reduceCropQuantity` to simulate a successful update
        when(cropRepository.reduceCropQuantity(CROP_ID, quantityToReduce)).thenReturn(1);

        // Create a mock Crop representing the state *after* reduction (quantity is 0)
        Crop deletedCropEntity = new Crop();
        deletedCropEntity.setId(CROP_ID);
        deletedCropEntity.setCropName("Wheat");
        deletedCropEntity.setCropType("Grain");
        deletedCropEntity.setCropPrice(100.0);
        deletedCropEntity.setCropQty(0); // Quantity becomes 0
        deletedCropEntity.setCropDescription("Freshly harvested wheat.");
        deletedCropEntity.setImageUrl("http://example.com/wheat.jpg");
        deletedCropEntity.setFarmer(testFarmer);

        when(cropRepository.findByIdWithFarmerAndUser(CROP_ID)).thenReturn(Optional.of(deletedCropEntity));
        // Mock the `delete` operation (it's a void method)
        doNothing().when(cropRepository).delete(any(Crop.class));

        // Call the service method
        CropPublicDTO result = dealerService.reduceCropQuantity(CROP_ID, quantityToReduce);

        // Assertions
        assertNull(result); // Should return null because the crop was deleted

        // Verifications
        verify(cropRepository, times(1)).findById(CROP_ID);
        verify(cropRepository, times(1)).reduceCropQuantity(CROP_ID, quantityToReduce);
        verify(cropRepository, times(1)).findByIdWithFarmerAndUser(CROP_ID);
        verify(cropRepository, times(1)).delete(deletedCropEntity); // Verify deletion was triggered
    }

    /**
     * Test case for reducing crop quantity with invalid input (e.g., zero or negative quantity to reduce).
     * Verifies that an IllegalArgumentException is thrown.
     */
    @Test
    void reduceCropQuantity_InvalidQuantityInput_ThrowsException() {
        // Test with zero quantity
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dealerService.reduceCropQuantity(CROP_ID, 0);
        });
        assertEquals("Quantity to reduce must be a positive integer.", exception.getMessage());

        // Test with negative quantity
        exception = assertThrows(IllegalArgumentException.class, () -> {
            dealerService.reduceCropQuantity(CROP_ID, -1);
        });
        assertEquals("Quantity to reduce must be a positive integer.", exception.getMessage());

        // Verifications: No interactions with repositories for invalid input
        verifyNoInteractions(cropRepository);
    }

    /**
     * Test case for reducing crop quantity when the specified crop is not found.
     * Verifies that a RuntimeException is thrown.
     */
    @Test
    void reduceCropQuantity_CropNotFound_ThrowsException() {
        // Mock `findById` to return empty Optional
        when(cropRepository.findById(CROP_ID)).thenReturn(Optional.empty());

        // Assert that a RuntimeException is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dealerService.reduceCropQuantity(CROP_ID, QUANTITY_TO_REDUCE);
        });

        // Verify the exception message
        assertEquals("Crop not found with ID: " + CROP_ID, exception.getMessage());

        // Verifications: Only `findById` should be called
        verify(cropRepository, times(1)).findById(CROP_ID);
        verify(cropRepository, never()).reduceCropQuantity(any(Long.class), any(Integer.class));
        verify(cropRepository, never()).findByIdWithFarmerAndUser(any(Long.class));
    }

    /**
     * Test case for reducing crop quantity when available stock is insufficient.
     * Verifies that an IllegalArgumentException is thrown.
     */
    @Test
    void reduceCropQuantity_InsufficientStock_ThrowsException() {
        // Set crop quantity to be less than the quantity to reduce
        testCrop.setCropQty(QUANTITY_TO_REDUCE - 1); // e.g., 4 if QUANTITY_TO_REDUCE is 5

        // Mock `findById` to return the crop with insufficient quantity
        when(cropRepository.findById(CROP_ID)).thenReturn(Optional.of(testCrop));

        // Assert that an IllegalArgumentException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dealerService.reduceCropQuantity(CROP_ID, QUANTITY_TO_REDUCE);
        });

        // Verify the exception message
        assertEquals("Cannot reduce quantity by " + QUANTITY_TO_REDUCE + ". Available quantity is only " + testCrop.getCropQty(), exception.getMessage());

        // Verifications: `findById` should be called, but not `reduceCropQuantity` or subsequent calls
        verify(cropRepository, times(1)).findById(CROP_ID);
        verify(cropRepository, never()).reduceCropQuantity(any(Long.class), any(Integer.class));
        verify(cropRepository, never()).findByIdWithFarmerAndUser(any(Long.class));
    }

    /**
     * Test case for successfully deleting a crop.
     * Verifies that the `deleteById` method of the repository is called.
     */
    @Test
    void deleteCrop_Success() {
        // Mock `existsById` to return true
        when(cropRepository.existsById(CROP_ID)).thenReturn(true);
        // Mock `deleteById` to do nothing (as it's a void method)
        doNothing().when(cropRepository).deleteById(CROP_ID);

        // Call the service method
        dealerService.deleteCrop(CROP_ID);

        // Verifications
        verify(cropRepository, times(1)).existsById(CROP_ID);
        verify(cropRepository, times(1)).deleteById(CROP_ID);
    }

    /**
     * Test case for deleting a crop that is not found.
     * Verifies that a RuntimeException is thrown.
     */
    @Test
    void deleteCrop_NotFound_ThrowsException() {
        // Mock `existsById` to return false
        when(cropRepository.existsById(CROP_ID)).thenReturn(false);

        // Assert that a RuntimeException is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dealerService.deleteCrop(CROP_ID);
        });

        // Verify the exception message
        assertEquals("Crop not found with ID: " + CROP_ID, exception.getMessage());

        // Verifications: `existsById` should be called, but `deleteById` should not
        verify(cropRepository, times(1)).existsById(CROP_ID);
        verify(cropRepository, never()).deleteById(any(Long.class));
    }
}
