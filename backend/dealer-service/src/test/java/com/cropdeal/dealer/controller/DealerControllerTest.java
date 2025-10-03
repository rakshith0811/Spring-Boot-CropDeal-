package com.cropdeal.dealer.controller;

import com.cropdeal.dealer.dto.CropPublicDTO;
import com.cropdeal.dealer.dto.DealerDTO;
import com.cropdeal.dealer.dto.DealerDTO2;
import com.cropdeal.dealer.entity.Dealer;
import com.cropdeal.dealer.entity.User;
import com.cropdeal.dealer.service.DealerService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DealerController.class) // Focuses on testing only the DealerController
class DealerControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform HTTP requests in tests

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON strings

    @MockBean // Mocks the DealerService dependency
    private DealerService dealerService;

    private Long DEALER_ID = 1L;
    private Long CROP_ID = 101L;

    private User testUser;
    private Dealer testDealer;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(DEALER_ID); // User ID is Long
        testUser.setUsername("testDealer");
        testUser.setMobileNumber("1234567890");
        testUser.setAddress("123 Dealer St");
        testUser.setActive(true);
        testUser.setRole("DEALER");

        testDealer = new Dealer();
        testDealer.setId(DEALER_ID); // Dealer ID is Long
        testDealer.setUser(testUser); // Correctly set the User object
    }

    /**
     * Test case for successfully retrieving a dealer's profile.
     * Verifies that the GET request to /api/dealer/profile/{dealerId} returns 200 OK
     * and the expected Dealer object.
     */
    @Test
    void getProfile_Success() throws Exception {
        when(dealerService.getProfile(DEALER_ID)).thenReturn(testDealer);

        mockMvc.perform(get("/api/dealer/profile/{dealerId}", DEALER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(DEALER_ID))
                .andExpect(jsonPath("$.user.username").value("testDealer"));

        verify(dealerService, times(1)).getProfile(DEALER_ID);
    }

    /**
     * Test case for retrieving a profile that does not exist.
     * Verifies that the controller handles RuntimeException from service and returns 404 Not Found.
     */
        /**
     * Test case for successfully updating a dealer's profile.
     * Verifies that the PUT request to /api/dealer/profile/{dealerId} returns 200 OK
     * and the updated Dealer object.
     */
    @Test
    void updateProfile_Success() throws Exception {
        DealerDTO updateDto = new DealerDTO();
        updateDto.setMobileNumber("9876543210");
        updateDto.setAddress("456 Updated St");

        Dealer updatedDealer = new Dealer();
        updatedDealer.setId(DEALER_ID);
        User updatedUser = new User();
        updatedUser.setId(DEALER_ID); // User ID is Long
        updatedUser.setUsername("testDealer");
        updatedUser.setMobileNumber("9876543210");
        updatedUser.setAddress("456 Updated St");
        updatedUser.setActive(true);
        updatedUser.setRole("DEALER");
        updatedDealer.setUser(updatedUser);

        when(dealerService.updateProfile(eq(DEALER_ID), any(DealerDTO.class))).thenReturn(updatedDealer);

        mockMvc.perform(put("/api/dealer/profile/{dealerId}", DEALER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.mobileNumber").value("9876543210"))
                .andExpect(jsonPath("$.user.address").value("456 Updated St"));

        verify(dealerService, times(1)).updateProfile(eq(DEALER_ID), any(DealerDTO.class));
    }

    /**
     * Test case for updating a profile that does not exist.
     * Verifies that the controller handles RuntimeException from service and returns 404 Not Found.
     */
       /**
     * Test case for successfully viewing all crops.
     * Verifies that the GET request to /api/dealer/crops returns 200 OK
     * and a list of CropPublicDTOs.
     */
    @Test
    void viewAllCrops_Success() throws Exception {
        CropPublicDTO crop1 = new CropPublicDTO();
        crop1.setId(1L);
        crop1.setCropName("Rice");
        crop1.setCropType("Grain");
        crop1.setCropQty(100);
        crop1.setCropPrice(25.0);
        crop1.setCropDescription("White Rice");
        crop1.setImageUrl("http://example.com/rice.jpg");
        crop1.setFarmerId(201L);
        crop1.setFarmerName("Farmer A");
        crop1.setFarmerMobile("1112223333");
        crop1.setFarmerAddress("Farm A");

        CropPublicDTO crop2 = new CropPublicDTO();
        crop2.setId(2L);
        crop2.setCropName("Corn");
        crop2.setCropType("Grain");
        crop2.setCropQty(200);
        crop2.setCropPrice(15.0);
        crop2.setCropDescription("Sweet Corn");
        crop2.setImageUrl("http://example.com/corn.jpg");
        crop2.setFarmerId(202L);
        crop2.setFarmerName("Farmer B");
        crop2.setFarmerMobile("4445556666");
        crop2.setFarmerAddress("Farm B");

        List<CropPublicDTO> mockCrops = Arrays.asList(crop1, crop2);

        when(dealerService.viewAllCropsWithFarmerInfo()).thenReturn(mockCrops);

        mockMvc.perform(get("/api/dealer/crops")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].cropName").value("Rice"))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].cropQty").value(100)); // Asserting on cropQty
                                                                    
        verify(dealerService, times(1)).viewAllCropsWithFarmerInfo();
    }

    /**
     * Test case for viewing all crops when no crops are available.
     * Verifies that the GET request returns 200 OK and an empty list.
     */
    @Test
    void viewAllCrops_EmptyList() throws Exception {
        when(dealerService.viewAllCropsWithFarmerInfo()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/dealer/crops")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(dealerService, times(1)).viewAllCropsWithFarmerInfo();
    }

    /**
     * Test case for successfully retrieving all dealers.
     * Verifies that the GET request to /api/dealer/all returns 200 OK
     * and a list of DealerDTO2s.
     */
    @Test
    void getAllDealers_Success() throws Exception {
        // Create actual Dealer entities with User objects as the service returns Dealer entities
        User user1 = new User(10L, "dealer1", "pass1", "DEALER", "111", "add1", true);
        User user2 = new User(20L, "dealer2", "pass2", "DEALER", "222", "add2", false);

        Dealer dealer1 = new Dealer(1L, user1);
        Dealer dealer2 = new Dealer(2L, user2);
        
        List<Dealer> mockDealers = Arrays.asList(dealer1, dealer2);

        when(dealerService.getAllDealers()).thenReturn(mockDealers);

        mockMvc.perform(get("/api/dealer/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("dealer1"))
                .andExpect(jsonPath("$[0].userId").value(1L)) // Assert on userId from DealerDTO2
                .andExpect(jsonPath("$[1].status").value(false)) // Assert on status from DealerDTO2
                .andExpect(jsonPath("$[1].mobileNumber").value("222"));

        verify(dealerService, times(1)).getAllDealers();
    }

    /**
     * Test case for retrieving all dealers when no dealers are available.
     * Verifies that the GET request returns 200 OK and an empty list.
     */
    @Test
    void getAllDealers_EmptyList() throws Exception {
        when(dealerService.getAllDealers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/dealer/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(dealerService, times(1)).getAllDealers();
    }

    /**
     * Test case for successfully reducing crop quantity.
     * Verifies that the PUT request returns 200 OK and the updated crop DTO.
     */
    @Test
    void reduceCropQuantity_Success() throws Exception {
        // Mock the CropPublicDTO that the service would return
        CropPublicDTO updatedCrop = new CropPublicDTO();
        updatedCrop.setId(CROP_ID);
        updatedCrop.setCropName("Updated Wheat");
        updatedCrop.setCropQty(5); // Correct field name as per CropPublicDTO

        Integer quantityToReduce = 5;

        when(dealerService.reduceCropQuantity(CROP_ID, quantityToReduce)).thenReturn(updatedCrop);

        mockMvc.perform(put("/api/dealer/crops/reduce-quantity/{cropId}", CROP_ID)
                .param("quantity", quantityToReduce.toString()) // RequestParam is String
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CROP_ID)) // Assert on 'id'
                .andExpect(jsonPath("$.cropQty").value(5)); // Assert on 'cropQty'

        verify(dealerService, times(1)).reduceCropQuantity(CROP_ID, quantityToReduce);
    }

    /**
     * Test case for reducing crop quantity resulting in deletion (quantity <= 0).
     * Verifies that the PUT request returns 200 OK and a success message.
     */
    @Test
    void reduceCropQuantity_Deleted() throws Exception {
        Integer quantityToReduce = 10; // Assuming this reduces quantity to 0 or less

        when(dealerService.reduceCropQuantity(CROP_ID, quantityToReduce)).thenReturn(null); // Service returns null for deletion

        mockMvc.perform(put("/api/dealer/crops/reduce-quantity/{cropId}", CROP_ID)
                .param("quantity", quantityToReduce.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Crop with ID " + CROP_ID + " quantity reduced to zero or less and deleted successfully."));

        verify(dealerService, times(1)).reduceCropQuantity(CROP_ID, quantityToReduce);
    }


    /**
     * Test case for reducing crop quantity with invalid input (e.g., negative quantity).
     * Verifies that the PUT request returns 400 Bad Request.
     */
    @Test
    void reduceCropQuantity_InvalidInput() throws Exception {
        Integer quantityToReduce = -5;

        when(dealerService.reduceCropQuantity(CROP_ID, quantityToReduce))
                .thenThrow(new IllegalArgumentException("Quantity to reduce must be a positive integer."));

        mockMvc.perform(put("/api/dealer/crops/reduce-quantity/{cropId}", CROP_ID)
                .param("quantity", quantityToReduce.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Quantity to reduce must be a positive integer."));

        verify(dealerService, times(1)).reduceCropQuantity(CROP_ID, quantityToReduce);
    }

    /**
     * Test case for reducing crop quantity when crop is not found.
     * Verifies that the PUT request returns 404 Not Found.
     */
    @Test
    void reduceCropQuantity_CropNotFound() throws Exception {
        Integer quantityToReduce = 5;

        when(dealerService.reduceCropQuantity(CROP_ID, quantityToReduce))
                .thenThrow(new RuntimeException("Crop not found with ID: " + CROP_ID));

        mockMvc.perform(put("/api/dealer/crops/reduce-quantity/{cropId}", CROP_ID)
                .param("quantity", quantityToReduce.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Crop not found with ID: " + CROP_ID));

        verify(dealerService, times(1)).reduceCropQuantity(CROP_ID, quantityToReduce);
    }

    /**
     * Test case for successfully deleting a crop.
     * Verifies that the DELETE request to /api/dealer/crops/{cropId} returns 200 OK.
     */
    @Test
    void deleteCrop_Success() throws Exception {
        doNothing().when(dealerService).deleteCrop(CROP_ID);

        mockMvc.perform(delete("/api/dealer/crops/{cropId}", CROP_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("Crop with ID " + CROP_ID + " deleted successfully."));

        verify(dealerService, times(1)).deleteCrop(CROP_ID);
    }

    /**
     * Test case for deleting a crop that is not found.
     * Verifies that the DELETE request returns 404 Not Found.
     */
    @Test
    void deleteCrop_NotFound() throws Exception {
        doThrow(new RuntimeException("Crop not found with ID: " + CROP_ID))
                .when(dealerService).deleteCrop(CROP_ID);

        mockMvc.perform(delete("/api/dealer/crops/{cropId}", CROP_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Crop not found with ID: " + CROP_ID));

        verify(dealerService, times(1)).deleteCrop(CROP_ID);
    }
}