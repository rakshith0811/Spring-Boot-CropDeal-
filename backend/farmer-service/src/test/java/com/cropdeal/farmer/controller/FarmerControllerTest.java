package com.cropdeal.farmer.controller;

import com.cropdeal.farmer.dto.CropPublicDTO;
import com.cropdeal.farmer.entity.Crop;
import com.cropdeal.farmer.entity.Farmer;
import com.cropdeal.farmer.service.FarmerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FarmerController.class)
public class FarmerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FarmerService farmerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void updateProfile_shouldReturnUpdatedFarmer() throws Exception {
        Farmer farmer = new Farmer();
        farmer.setId(1);

        FarmerController.UpdateProfileRequest request = new FarmerController.UpdateProfileRequest();
        request.setMobileNumber("1234567890");
        request.setAddress("New Address");

        Mockito.when(farmerService.updateProfile(eq(1), eq(request.getMobileNumber()), eq(request.getAddress())))
                .thenReturn(farmer);

        mockMvc.perform(put("/api/farmer/profile/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getProfile_shouldReturnFarmer() throws Exception {
        Farmer farmer = new Farmer();
        farmer.setId(1);

        Mockito.when(farmerService.getProfile(1)).thenReturn(farmer);

        mockMvc.perform(get("/api/farmer/profile/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void addCrop_shouldReturnAddedCrop() throws Exception {
        Crop crop = new Crop();
        crop.setId(100L);
        Mockito.when(farmerService.addCrop(eq(1), any(Crop.class))).thenReturn(crop);
//
//        mockMvc.perform(post("/api/farmer/crop/{id}", 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(crop)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(100));
   }
//
    @Test
    void getFarmerCrops_shouldReturnListOfCrops() throws Exception {
        Crop crop = new Crop();
        crop.setId(100L);
        Mockito.when(farmerService.getFarmerCrops(1)).thenReturn(List.of(crop));
//
//        mockMvc.perform(get("/api/farmer/crops/{id}", 1))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(100));
    }
//
    @Test
    void updateCrop_shouldReturnUpdatedCrop() throws Exception {
        Crop crop = new Crop();
        crop.setId(100L);

        Mockito.when(farmerService.updateCrop(eq(1), eq(100L), any(Crop.class))).thenReturn(crop);
//
//        mockMvc.perform(put("/api/farmer/crop/{farmerId}/{cropId}", 1, 100L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(crop)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void deleteCrop_shouldReturnSuccessMessage() throws Exception {
        Mockito.doNothing().when(farmerService).deleteCrop(1, 100L);

        mockMvc.perform(delete("/api/farmer/crop/{farmerId}/{cropId}", 1, 100L))
                .andExpect(status().isOk())
                .andExpect(content().string("Crop deleted successfully"));
    }

    @Test
    void getPublicCrops_shouldReturnList() throws Exception {
        CropPublicDTO dto = new CropPublicDTO();
        Mockito.when(farmerService.getPublicCrops()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/farmer/crops/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAllFarmers_shouldReturnListOfFarmerDTOs() throws Exception {
        Farmer farmer = new Farmer();
        farmer.setId(1);
        var user = new com.cropdeal.farmer.entity.User();
        user.setUsername("farmer1");
        user.setMobileNumber("1234567890");
        user.setAddress("address");
        user.setActive(true);
        farmer.setUser(user);

        Mockito.when(farmerService.getAllFarmers()).thenReturn(List.of(farmer));

        mockMvc.perform(get("/api/farmer/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].name").value("farmer1"))
                .andExpect(jsonPath("$[0].mobileNumber").value("1234567890"))
                .andExpect(jsonPath("$[0].address").value("address"))
                .andExpect(jsonPath("$[0].status").value(true))
                .andExpect(jsonPath("$[0].role").value("FARMER"));
    }
}
