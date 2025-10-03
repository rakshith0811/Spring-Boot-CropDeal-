package com.cropdeal.farmer.controller;

import com.cropdeal.farmer.dto.CropPublicDTO;
import com.cropdeal.farmer.dto.FarmerDTO;
import com.cropdeal.farmer.entity.Crop;
import com.cropdeal.farmer.entity.Farmer;
import com.cropdeal.farmer.service.FarmerService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmer")
public class FarmerController {

    @Autowired
    private FarmerService farmerService;

    //private FarmerDTO dto;
    // Update profile (mobile, address)
    @PutMapping("/profile/{id}")
    public Farmer updateProfile(@PathVariable Integer id, @RequestBody UpdateProfileRequest request) {
        return farmerService.updateProfile(id, request.getMobileNumber(), request.getAddress());
    }

    @Data
    static class UpdateProfileRequest {
        private String mobileNumber;
        private String address;
    }

    // Get farmer profile
    @GetMapping("/profile/{id}")
    public Farmer getProfile(@PathVariable Integer id) {
        return farmerService.getProfile(id);
    }

    // Add crop
    @PostMapping("/crop/{id}")
    public Crop addCrop(@PathVariable Integer id, @RequestBody Crop crop) {
        return farmerService.addCrop(id, crop);
    }

    // Get all crops of farmer
    @GetMapping("/crops/{id}")
    public List<Crop> getFarmerCrops(@PathVariable Integer id) {
        return farmerService.getFarmerCrops(id);
    }
 // Update crop
    @PutMapping("/crop/{farmerId}/{cropId}")
    public Crop updateCrop(@PathVariable Integer farmerId, @PathVariable Long cropId, @RequestBody Crop crop) {
        return farmerService.updateCrop(farmerId, cropId, crop);
    }

    // Delete crop
    @DeleteMapping("/crop/{farmerId}/{cropId}")
    public String deleteCrop(@PathVariable Integer farmerId, @PathVariable Long cropId) {
        try {
            farmerService.deleteCrop(farmerId, cropId);
            return "Crop deleted successfully";
        } catch (RuntimeException e) {
            return e.getMessage(); // e.g., "Crop is ordered and cannot be deleted"
        }
    }



    // Get all public crops (for buyers to browse)
    @GetMapping("/crops/public")
    public List<CropPublicDTO> getPublicCrops() {
        return farmerService.getPublicCrops();
    }
    @GetMapping("/all")
    public List<FarmerDTO> getAllFarmers() {
        List<Farmer> farmers = farmerService.getAllFarmers();

        // Convert entity to DTO with role
        return farmers.stream().map(farmer -> {
            FarmerDTO dto = new FarmerDTO();
            dto.setUserId(farmer.getId());
            dto.setName(farmer.getUser().getUsername());
            dto.setMobileNumber(farmer.getUser().getMobileNumber());
            dto.setAddress(farmer.getUser().getAddress());
            dto.setStatus(farmer.getUser().getActive());
            dto.setRole("FARMER");  
            return dto;
        }).toList();
    }

}
