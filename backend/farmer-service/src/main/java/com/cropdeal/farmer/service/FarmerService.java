package com.cropdeal.farmer.service;

import com.cropdeal.farmer.dto.CropPublicDTO;
import com.cropdeal.farmer.entity.Crop;
import com.cropdeal.farmer.entity.Farmer;

import java.util.List;

public interface FarmerService {

    Farmer updateProfile(Integer farmerId, String mobile, String address);

    Crop addCrop(Integer farmerId, Crop crop);

    List<Crop> getFarmerCrops(Integer farmerId);

    List<CropPublicDTO> getPublicCrops();
    Crop updateCrop(Integer farmerId, Long cropId, Crop updatedCrop);

    void deleteCrop(Integer farmerId, Long cropId);


    Farmer getProfile(Integer farmerId);
    
    List<Farmer> getAllFarmers();
}
